package io.blockv.core.model

/**
 * Created by LordCheddar on 2018/02/26.
 */
class Commerce(var isRedeemable: Boolean?,
               var pricing: Pricing?) {

  override fun toString(): String {
    return "Commerce{" +
      "isRedeemable='" + isRedeemable + '\'' +
      ", pricing='" + pricing + '\'' +
      "}"
  }
}