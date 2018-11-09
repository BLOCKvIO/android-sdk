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

class Pricing {

  @Serializer.Serialize
  var pricingType: String?
  @Serializer.Serialize(path = "value")
  var currency: String?
  @Serializer.Serialize(path = "value")
  var price: String?
  @Serializer.Serialize(name = "valid_from", path = "value")
  var validFrom: String?
  @Serializer.Serialize(name = "valid_through", path = "value")
  var validThrough: String?
  @Serializer.Serialize(name = "vat_included", path = "value")
  var isVatIncluded: Boolean

  @Serializer.Serializable
  constructor(
    pricingType: String?,
    currency: String?,
    price: String?,
    validFrom: String?,
    validThrough: String?,
    isVatIncluded: Boolean
  ) {
    this.pricingType = pricingType
    this.currency = currency
    this.price = price
    this.validFrom = validFrom
    this.validThrough = validThrough
    this.isVatIncluded = isVatIncluded
  }

  constructor(pricing: Pricing) : this(
    pricing.pricingType,
    pricing.currency,
    pricing.price,
    pricing.validFrom,
    pricing.validThrough,
    pricing.isVatIncluded
  )

  override fun toString(): String {
    return "Pricing{" +
      "pricingType='" + pricingType + '\'' +
      ", currency='" + currency + '\'' +
      ", price='" + price + '\'' +
      ", validFrom='" + validFrom + '\'' +
      ", validThrough='" + validThrough + '\'' +
      ", isVatIncluded='" + isVatIncluded + '\'' +
      "}"
  }


}
