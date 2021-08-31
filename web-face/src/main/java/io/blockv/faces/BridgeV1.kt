package io.blockv.faces

import android.util.Log
import io.blockv.common.internal.net.rest.exception.BlockvException
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.blockv.common.util.JsonUtil
import io.blockv.face.client.FaceBridge
import io.blockv.face.client.manager.MessageManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject

internal class BridgeV1(
  bridge: FaceBridge,
  vatom: Vatom,
  face: Face,
  sendMessage: (id: String, payload: String) -> Unit
) : BaseBridge(bridge, sendMessage, vatom, face) {
  override fun onVatomUpdate(vatom: Vatom) {

    collect(Single.fromCallable {
      encodeVatom(vatom) ?: throw NullPointerException()
    }
      .subscribeOn(Schedulers.computation())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        sendMessage(
          "vatom.updated",
          it
        )
      }, {})
    )
  }

  override fun onMessage(message: Bridge.Message) {
    Log.i("bridgev1", message.name)
    val responseId = message.requestId

    when (message.name) {
      "vatom.init" -> init(responseId)
      "vatom.children.get" -> getChildren(responseId)
      "vatom.get" -> getVatom(responseId, message.payload?.optString("id", null))
      "vatom.performAction" -> performAction(
        responseId,
        message.payload?.optString("actionName", null),
        message.payload?.optJSONObject("actionData")
      )
      "user.profile.fetch" -> getPublicUser(responseId, message.payload?.optString("userID", null))
      "user.avatar.fetch" -> getPublicUserAvatar(responseId, message.payload?.optString("userID", null))
      else -> {
        if (responseId == null) return

        val wrappedMessage = wrapCustomMessage(message.name, message.payload)
        //custom
        collect(
          this.faceBridge.messageManager
            .sendMessage(wrappedMessage.first, wrappedMessage.second)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              sendUnWrappedResponse(responseId, wrappedMessage.first, it.payload)
            }, {
              val errorPayload = JSONObject()
                .put(
                  "errorCode",
                  if (it is MessageManager.MessageException) it.error.name.toLowerCase() else "viewer_error"
                ).put(
                  "errorText",
                  it.message
                )
              sendMessage(responseId, errorPayload)
            })
        )

      }
    }

  }

  private fun encodeVatom(vatom: Vatom): JSONObject? {

    val data = faceBridge.jsonSerializer.serialize(vatom)
    if (data != null) {
      val out = JSONObject()
      val properties = data.getJSONObject("vAtom::vAtomType")
      val private = data.optJSONObject("private")
      if (private != null) {
        JsonUtil.merge(properties, private)
      }
      val vatomInfo = JSONObject()
      vatomInfo.put("id", vatom.id)
      vatomInfo.put("properties", properties)

      val resourceMap = JSONObject()
      val resources = properties.optJSONArray("resources") ?: JSONArray()
      (0 until resources.length()).forEach {
        val name = resources.getJSONObject(it).optString("name")
        var url = resources.getJSONObject(it).optJSONObject("value")?.optString("value")
        if (url != null) {
          url = faceBridge.resourceManager.resourceEncoder.encodeUrl(url)
        }
        resources.getJSONObject(it).optJSONObject("value")?.put("value", url)
        if (name != null) {
          resourceMap.put(name, url)
        }
      }
      vatomInfo.put("resources", resourceMap)

      out.put("vatomInfo", vatomInfo)
      return out
    }
    return null
  }

  private fun init(repsonseId: String?) {

    collect(
      faceBridge.userManager
        .getCurrentUser()
        .observeOn(Schedulers.computation())
        .map { publicUser ->

          val response = encodeVatom(vatom) ?: throw NullPointerException()

          val faceProperty = faceBridge.jsonSerializer.serialize(face.property)

          val viewer = JSONObject()
          response.put("viewer", viewer)
          response.optJSONObject("vatomInfo")?.put("faceProperties", faceProperty)

          val user = JSONObject()
          user.put("id", publicUser.id)
          user.put("firstName", publicUser.firstName)
          user.put("lastName", publicUser.lastName)
          user.put(
            "avatarURL",
            if (publicUser.avatarUri != null) faceBridge.resourceManager.resourceEncoder.encodeUrl(publicUser.avatarUri!!) else null
          )
          user.put("displayName", publicUser.name)

          response.put("user", user)

          response
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          sendMessage(repsonseId ?: "vatom.init-complete", it)
        }, {
          sendMessage(
            repsonseId ?: "vatom.init-complete",
            JSONObject().put("errorCode", if (it is BlockvException) it.blockvCode else "0").put(
              "errorText",
              it.message
            )
          )
        })
    )
  }

  private fun getChildren(repsonseId: String?) {

    collect(faceBridge
      .vatomManager
      .getInventory(vatom.id, 1, 100)
      .observeOn(Schedulers.computation())
      .map { list ->
        val vatoms = JSONArray()
        list.forEach {
          val vatom = encodeVatom(it)
          if (vatom != null) {
            vatoms.put(vatom)
          }
        }
        val response = JSONObject()
        response.put("items", vatoms)
        response
      }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        sendMessage(repsonseId ?: "vatom.children.get-response", it)
      }, {
        sendMessage(
          repsonseId ?: "vatom.children.get-response",
          JSONObject().put("errorCode", if (it is BlockvException) it.blockvCode else "0").put(
            "errorText",
            it.message
          )
        )
      })
    )

  }

  private fun getVatom(repsonseId: String?, vatomId: String?) {

    if (vatomId == null) {
      sendMessage(
        repsonseId ?: "vatom.get-response",
        JSONObject().put("errorCode", "0").put(
          "errorText",
          "Null vatom Id"
        )
      )
      return
    }
    collect(faceBridge
      .vatomManager
      .getVatoms(vatomId)
      .map { vatoms ->
        val vatom = vatoms[0]
        Pair(encodeVatom(vatom) ?: throw NullPointerException(), vatom)
      }
      .subscribe({
        if (it.second.id == this.vatom.id || it.second.property.parentId == this.vatom.id) {
          sendMessage(repsonseId ?: "vatom.get-response", it.first)
        } else
          sendMessage(
            repsonseId ?: "vatom.get-response",
            JSONObject().put("errorCode", "0").put(
              "errorText",
              "Security Error, vatom id $vatomId is not in scope."
            )
          )

      }, {
        sendMessage(
          repsonseId ?: "vatom.get-response",
          JSONObject().put("errorCode", if (it is BlockvException) it.blockvCode else "0").put(
            "errorText",
            it.message
          )
        )
      })
    )

  }

  private fun performAction(repsonseId: String?, action: String?, payload: JSONObject?) {

    if (action == null || payload == null) {
      sendMessage(
        repsonseId ?: "vatom.performAction-complete",
        JSONObject().put("errorCode", "0").put(
          "errorText",
          "Null action or payload"
        )
      )
      return
    }
    if (payload["this.id"] != vatom.id) {
      sendMessage(
        repsonseId ?: "vatom.performAction-complete", JSONObject()
          .put("errorCode", "invalid_permission")
          .put("errorMessage", "You do not have permission to do perform this action.")
      )
    } else
      collect(
        faceBridge
          .vatomManager
          .performAction(action, payload)
          .subscribe({
            sendMessage(repsonseId ?: "vatom.performAction-complete", it)
          }, {
            sendMessage(
              repsonseId ?: "vatom.performAction-complete",
              JSONObject().put("errorCode", if (it is BlockvException) it.blockvCode else "0").put(
                "errorText",
                it.message
              )
            )
          })
      )
  }

  private fun getPublicUser(repsonseId: String?, userId: String?) {
    if (userId == null) {
      sendMessage(
        repsonseId ?: "user.profile.fetch-complete",
        JSONObject().put("errorCode", "0").put(
          "errorText",
          "Null user Id"
        )
      )
      return
    }
    collect(
      faceBridge
        .userManager
        .getPublicUser(userId)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          val response = JSONObject()
          response.put("firstName", it?.firstName)
          response.put("lastName", it?.lastName)
          sendMessage(repsonseId ?: "user.profile.fetch-complete", response)
        }, {
          sendMessage(
            repsonseId ?: "user.profile.fetch-complete",
            JSONObject().put("errorCode", if (it is BlockvException) it.blockvCode else "0").put(
              "errorText",
              it.message
            )
          )
        })
    )

  }

  private fun getPublicUserAvatar(responseId: String?, userId: String?) {
    if (userId == null) {
      sendMessage(
        responseId ?: "user.avatar.fetch",
        JSONObject().put("errorCode", "0").put(
          "errorText",
          "Null user Id"
        )
      )
      return
    }
    collect(
      faceBridge
        .userManager
        .getPublicUser(userId)
        .observeOn(Schedulers.computation())
        .map {
          if (it.avatarUri != null) {
            faceBridge.resourceManager.resourceEncoder.encodeUrl(it.avatarUri!!)
          } else
            ""
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          val response = JSONObject()
          response.put("avatarURL", it)
          sendMessage(responseId ?: "user.avatar.fetch-complete", response)
        }, {
          sendMessage(
            responseId ?: "user.avatar.fetch-complete",
            JSONObject().put("errorCode", if (it is BlockvException) it.blockvCode else "0").put(
              "errorText",
              it.message
            )
          )
        })
    )
  }

  fun wrapCustomMessage(name: String, payload: JSONObject?): Pair<String, JSONObject> {

    val data = payload ?: JSONObject()
    return when (name) {
      "ui.vatom.show" -> Pair("viewer.vatom.show", data)
      "vatom.view.close" -> Pair("viewer.view.close", data)
      "ui.map.show" -> Pair("viewer.map.show", data)
      "ui.qr.scan" -> Pair("viewer.qr.scan", data)
      "ui.browser.open" -> Pair("viewer.url.open", data)
      "ui.scanner.show" -> Pair("viewer.scanner.show", data)
      "ui.file.open" -> Pair("viewer.url.open", data)
      "ui.vatom.transfer" -> Pair("viewer.action.send", data)
      "ui.vatom.clone" -> Pair("viewer.action.share", data)
      "vatom.view.presentCard" -> Pair("viewer.card.show", data)
      else -> Pair(name, data)
    }
  }

  fun sendUnWrappedResponse(responseId: String, name: String, payload: JSONObject) {

    when (name) {
      "viewer.vatom.show" -> sendMessage("ui.vatom.show", payload)
      "viewer.view.close" -> sendMessage("vatom.view.close", payload)
      "viewer.map.show" -> sendMessage("ui.map.show", payload)
      "viewer.qr.scan" -> sendMessage("ui.qr.scan", payload.optString("data"))
      "viewer.url.open" -> sendMessage("ui.browser.open", payload)
      "viewer.scanner.show" -> sendMessage("ui.scanner.show", payload)
      "viewer.card.show" -> sendMessage("vatom.view.presentCard", payload)
      "viewer.action.send" -> sendMessage("ui.vatom.transfer", payload)
      "viewer.action.share" -> sendMessage("ui.vatom.clone", payload)
      else -> sendMessage(responseId, payload)
    }
  }

  fun sendMessage(id: String, payload: JSONObject) {
    this.sendMessage(id, payload.toString())
  }

}