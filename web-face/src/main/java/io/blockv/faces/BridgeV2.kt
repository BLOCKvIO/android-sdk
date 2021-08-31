package io.blockv.faces

import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.blockv.face.client.FaceBridge
import io.blockv.face.client.manager.MessageManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal class BridgeV2(
  bridge: FaceBridge,
  vatom: Vatom,
  face: Face,
  sendMessage: (id: String, payload: String) -> Unit
) : BaseBridge(bridge, sendMessage, vatom, face) {

  override fun onVatomUpdate(vatom: Vatom) {
    collect(Single.fromCallable {
      faceBridge.jsonSerializer.serialize(vatom)
    }
      .subscribeOn(Schedulers.computation())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        sendRequestMessage("req-update", "core.vatom.update", JSONObject().put("vatom", it))
      }, {

      }))
  }

  override fun onMessage(message: Bridge.Message) {
    if (message.requestId == null) return
    try {
      when (message.name) {
        "core.init" -> init(message.requestId)
        "core.user.get" -> getUser(
          message.requestId,
          message.payload!!.getString("id")
        )
        "core.vatom.get" -> {
          val id = message.payload!!.getString("id")
          getVatom(
            message.requestId,
            id
          )
        }
        "core.vatom.children.get" -> getChildren(
          message.requestId,
          message.payload!!.getString("id")
        )
        "core.action.perform" -> {
          performAction(
            message.requestId,
            message.payload!!.getString("action_name"),
            message.payload.getJSONObject("payload")
          )
        }
        "core.resource.encode" -> {
          val urls = message.payload!!.getJSONArray("urls")
          encodeResource(
            message.requestId,
            *(0 until urls.length()).map {
              urls.getString(it)
            }.toTypedArray()
          )
        }
        else -> {
          if (message.name.startsWith("viewer.")) {
            collect(
              this.faceBridge
                .messageManager
                .sendMessage(message.name, message.payload ?: JSONObject())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                  sendMessage(message.requestId, message.payload)
                }, {
                  if (it is MessageManager.MessageException) {
                    sendErrorMessage(
                      message.requestId,
                      it.error.name.toLowerCase(),
                      it.message ?: ""
                    )
                  } else
                    sendErrorMessage(message.requestId, Error.VIEWER_ERROR.code, it.message ?: "")
                })
            )
          }
        }
      }
    } catch (e: Exception) {
      if (e is NullPointerException || e is JSONException) {
        sendErrorMessage(message.requestId, Error.INVALID_REQUEST)
      } else {
        e.printStackTrace()
        sendErrorMessage(message.requestId, Error.FACE_ERROR.code, e.message ?: "")
      }
    }
  }

  fun init(responseId: String) {
    collect(Single.fromCallable {
      val vatomJson = faceBridge.jsonSerializer.serialize(vatom)
      val faceJson = faceBridge.jsonSerializer.serialize(face)
      JSONObject()
        .put("vatom", vatomJson)
        .put("face", faceJson)
    }
      .subscribeOn(Schedulers.computation())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        sendMessage(responseId, it)
      }, {
        sendErrorMessage(responseId, Error.FACE_ERROR.code, it.message ?: "")
      }))
  }

  fun getUser(responseId: String, userId: String) {
    collect(
      faceBridge.userManager.getPublicUser(userId)
        .observeOn(Schedulers.computation())
        .map {
          faceBridge.jsonSerializer.serialize(it)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          sendMessage(responseId, JSONObject().put("user", it))
        }, {
          sendErrorMessage(responseId, Error.FACE_ERROR.code, it.message ?: "")
        })
    )
  }

  fun getVatom(responseId: String, vatomId: String) {
    collect(
      faceBridge.vatomManager
        .getVatoms(vatomId)
        .observeOn(Schedulers.computation())
        .map {
          Pair(faceBridge.jsonSerializer.serialize(it[0]), it[0])
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          if (it.second.id == vatom.id
            || it.second.property.parentId == vatom.id
          ) {
            sendMessage(responseId, JSONObject().put("vatom", it.first))
          } else
            sendErrorMessage(responseId, Error.INVALID_PERMISSION)
        }, {
          sendErrorMessage(responseId, Error.FACE_ERROR.code, it.message ?: "")
        })
    )
  }

  fun getChildren(responseId: String, vatomId: String) {
    if (vatomId != vatom.id) {
      sendErrorMessage(responseId, Error.INVALID_PERMISSION)
    } else
      collect(faceBridge.vatomManager
        .getInventory(vatomId, 1, 100)
        .observeOn(Schedulers.computation())
        .map { vatoms ->
          val jsonArray = JSONArray()
          vatoms.forEach {
            jsonArray.put(faceBridge.jsonSerializer.serialize(it))
          }
          jsonArray
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          sendMessage(responseId, JSONObject().put("vatoms", it))
        }, {
          sendErrorMessage(responseId, Error.FACE_ERROR.code, it.message ?: "")
        })
      )
  }

  fun performAction(
    responseId: String,
    action: String,
    payload: JSONObject
  ) {
    if (payload["this.id"] != vatom.id) {
      sendErrorMessage(responseId, Error.INVALID_PERMISSION)
    } else
      collect(
        faceBridge.vatomManager
          .performAction(action, payload)
          .subscribe({
            sendMessage(responseId, it)
          }, {
            sendErrorMessage(responseId, Error.FACE_ERROR.code, it.message ?: "")
          })
      )
  }

  fun encodeResource(responseId: String, vararg urls: String) {

    collect(
      Single.fromCallable {
        urls.map {
          faceBridge
            .resourceManager
            .resourceEncoder
            .encodeUrl(it)
        }
      }.subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.computation())
        .map {
          val array = JSONArray()
          it.forEach {
            array.put(it)
          }
          array
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          sendMessage(responseId, JSONObject().put("urls", it))
        }, {
          sendErrorMessage(responseId, Error.FACE_ERROR.code, it.message ?: "")
        })
    )
  }

  fun sendErrorMessage(responseId: String, code: String, message: String) {
    sendMessage(
      responseId, JSONObject()
        .put("error_code", code)
        .put("error_message", message)
    )
  }

  fun sendErrorMessage(responseId: String, error: Error) {
    sendErrorMessage(responseId, error.code, error.message)
  }


  fun sendRequestMessage(requestId: String, name: String, payload: JSONObject?) {
    val out = JSONObject()
    out.put("request_id", requestId)
    out.put("name", name)
    out.put("source", "blockv_vatoms_android")
    out.put("version", "1.0.0")
    out.put("payload", payload ?: JSONObject())
    super.sendMessage(name, out.toString())
  }

  fun sendMessage(responseId: String, payload: JSONObject?) {
    val out = JSONObject()
    out.put("response_id", responseId)
    out.put("payload", payload ?: JSONObject())
    super.sendMessage(responseId, out.toString())
  }

  enum class Error(
    val code: String,
    val message: String
  ) {
    INVALID_REQUEST("invalid_request", "The request structure is not supported."),
    INVALID_PERMISSION("invalid_permission", "You do not have the required permissions to do this action."),
    FACE_ERROR("face_error", "An error has occurred in the Face."),
    VIEWER_ERROR("viewer_error", "An error has occurred in the viewer.")
  }

}