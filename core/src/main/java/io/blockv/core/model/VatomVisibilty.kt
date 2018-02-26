package io.blockv.core.model

/**
 * Created by LordCheddar on 2018/02/26.
 */
class VatomVisibilty(var type: String,
                     var value: String) {

  override fun toString(): String {
    return "VatomVisibility{" +
      "type='" + type + '\'' +
      ", value='" + value + '\'' +
      '}'
  }

}