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

import io.blockv.common.internal.json.serializer.Serializer

class Commerce : Model {

  @Serializer.Serialize
  var pricing: Pricing?

  @Serializer.Serializable
  constructor(pricing: Pricing?) {
    this.pricing = pricing
  }

  constructor(commerce: Commerce) : this(if (commerce.pricing != null) Pricing(commerce.pricing!!) else null)

  override fun toString(): String {
    return "Commerce{" +
      "pricing='" + pricing + '\'' +
      "}"
  }
}