/*
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.common.model

class Face(
  var id: String,
  var templateId: String,
  var createdBy: String?,
  var whenCreated: String?,
  var whenModified: String?,
  var property: FaceProperty
) {
  private var native: Boolean? = null
  private var web: Boolean? = null

  @Synchronized
  fun isNative(): Boolean {
    if (native == null) {
      native = property.displayUrl.startsWith("native://")
    }
    return native!!
  }

  @Synchronized
  fun isWeb(): Boolean {
    if (web == null) {
      web = property.displayUrl.startsWith("https://")
    }
    return web!!
  }

  override fun toString(): String {
    return "Face{" +
      "id='" + id + '\'' +
      ", templateId='" + templateId + '\'' +
      ", createdBy='" + createdBy + '\'' +
      ", whenCreated='" + whenCreated + '\'' +
      ", whenModified='" + whenModified + '\'' +
      "," + property + '\'' +
      "}"
  }


}
