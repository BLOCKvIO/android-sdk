package io.blockv.core.model

class Face(var id: String?,
           var templateId: String?,
           var createdBy: String?,
           var whenCreated: String?,
           var whenModified: String?,
           var property: FaceProperty?) {


  override fun toString(): String {
    return "Face{" +
      "id='" + id + '\'' +
      ", templateId='" + templateId + '\'' +
      ", createdBy='" + createdBy + '\'' +
      ", whenCreated='" + whenCreated + '\'' +
      ", whenModified='" + whenModified + '\'' +
      "," + property+ '\'' +
    "}"
  }


}
