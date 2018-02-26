package io.blockv.core.model

class FaceProperty(var displayUrl: String?,
                   var bluetoothLe: Boolean?,
                   var contactList: Boolean?,
                   var threed: Boolean?,
                   var gps: Boolean?,
                   var viewMode: String?,
                   var platform: String?,
                   var quality: String?,
                   var resources: List<String>?) {


  override fun toString(): String {
    return "FaceProperty{" +
      "displayUrl='" + displayUrl + '\'' +
      ", isBluetoothLe='" + bluetoothLe + '\'' +
      ", isContactList='" + contactList + '\'' +
      ", isThreed='" + threed + '\'' +
      ", isGps='" + gps + '\'' +
      ", viewMode='" + viewMode + '\'' +
      ", platform='" + platform + '\'' +
      ", quality='" + quality + '\'' +
      ", resources='" + resources + '\'' +
      "}"
  }
}

