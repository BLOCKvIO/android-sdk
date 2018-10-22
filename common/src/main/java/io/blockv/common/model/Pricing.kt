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

import io.blockv.common.internal.json.JsonModule

class Pricing {

  @JsonModule.Serialize
  var priceType: String?
  @JsonModule.Serialize(path = "value")
  var currency: String?
  @JsonModule.Serialize(path = "value")
  var price: String?
  @JsonModule.Serialize(name = "valid_from", path = "value")
  var validFrom: String?
  @JsonModule.Serialize(name = "valid_through", path = "value")
  var validThrough: String?
  @JsonModule.Serialize(name = "vat_included", path = "value")
  var isVatIncluded: Boolean

  @JsonModule.Serializable
  constructor(
    priceType: String?,
    currency: String?,
    price: String?,
    validFrom: String?,
    validThrough: String?,
    isVatIncluded: Boolean
  ) {
    this.priceType = priceType
    this.currency = currency
    this.price = price
    this.validFrom = validFrom
    this.validThrough = validThrough
    this.isVatIncluded = isVatIncluded
  }

  override fun toString(): String {
    return "Pricing{" +
      "priceType='" + priceType + '\'' +
      ", currency='" + currency + '\'' +
      ", price='" + price + '\'' +
      ", validFrom='" + validFrom + '\'' +
      ", validThrough='" + validThrough + '\'' +
      ", isVatIncluded='" + isVatIncluded + '\'' +
      "}"
  }


}
