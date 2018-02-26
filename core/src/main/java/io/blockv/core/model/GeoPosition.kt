package io.blockv.core.model

class GeoPosition(var type:String?,var reqType:String?,var coordinates:List<Float>?) {

  override fun toString(): String {
    return "GeoPos{" +
      "type='" + type + '\'' +
      ", reqType='" + reqType + '\'' +
      ", coordinates=" + coordinates +
      '}'
  }
}
