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
package io.blockv.core.model

class Resource(var name: String,
               var type: String,
               var url: String) {

  override fun toString(): String {
    return "Resource{" +
      "name='" + name + '\'' +
      ", type='" + type + '\'' +
      ", url='" + url + '\'' +
      '}'
  }

  override fun equals(o: Any?): Boolean {
    if (this === o) return true
    if (o !is Resource) return false
    val resource = o
    if (name != resource.name) return false
    if (type != resource.type) return false
    return url == resource.url
  }

  override fun hashCode(): Int {
    return (name+type+url).hashCode()
  }

}
