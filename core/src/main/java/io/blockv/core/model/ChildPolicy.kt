package io.blockv.core.model

class ChildPolicy(var count: Int?,
                  var templateVariation: String?,
                  var creationPolicy: CreationPolicy?) {

  override fun toString(): String {
    return "ChildPolicy{" +
      "count='" + count + '\'' +
      ", templateVariation='" + templateVariation + '\'' +
      "," +creationPolicy + '\'' +
      "}"
  }

}
