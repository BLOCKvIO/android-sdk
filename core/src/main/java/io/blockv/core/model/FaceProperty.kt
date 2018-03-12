package io.blockv.core.model

class FaceProperty(var displayUrl: String?,
                   var viewMode: String?,
                   var platform: String?,
                   var resources: List<String>?) {


  override fun toString(): String {
    return "FaceProperty{" +
      "displayUrl='" + displayUrl + '\'' +
      ", viewMode='" + viewMode + '\'' +
      ", platform='" + platform + '\'' +
      ", resources='" + resources + '\'' +
      "}"
  }
}

