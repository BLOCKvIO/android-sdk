package io.blockv.rxface.client

class Optional<T>(val value: T?) {

  fun isEmpty(): Boolean {
    return value == null
  }

}