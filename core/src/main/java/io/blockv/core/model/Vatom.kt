package io.blockv.core.model

import org.json.JSONObject

class Vatom(var id: String?,
            var whenCreated: String?,
            var whenModified: String?,
            var property: VatomProperty?,
            var private: JSONObject?) {

  override fun toString(): String {
    return "Vatom{" +
      "whenCreated='" + whenCreated + '\'' +
      ", whenModified='" + whenModified + '\'' +
      "," + property + '\'' +
      ", private='" + private + '\'' +
      "}"
  }

  override fun equals(o: Any?): Boolean {
    if (this === o) return true
    if (o !is Vatom) return false
    return id == o.id
  }

  override fun hashCode(): Int {
    return id!!.hashCode()
  }
}
