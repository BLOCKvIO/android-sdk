package io.blockv.core.model

/**
 * Created by LordCheddar on 2018/02/26.
 */
class Commerce(var pricing: Pricing?) {

  override fun toString(): String {
    return "Commerce{" +
      "pricing='" + pricing + '\'' +
      "}"
  }
}