package io.blockv.core.model

class Pricing(var priceType: String?,
              var currency: String?,
              var price: String?,
              var validFrom: String?,
              var validThrough: String?,
              var isVatIncluded: Boolean) {

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
