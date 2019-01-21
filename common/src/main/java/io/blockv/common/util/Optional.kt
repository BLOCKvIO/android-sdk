package io.blockv.common.util

class Optional<T>(val value: T?) {

  fun isEmpty(): Boolean {
    return value == null
  }

}