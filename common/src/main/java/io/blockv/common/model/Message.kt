package io.blockv.common.model

class Message<T>(val items: List<T> = ArrayList(), val type: Type, val state: State) {

  constructor(item: T, type: Type, state: State) : this(listOf(item), type, state)

  enum class State {
    STABLE,
    UNSTABLE,
    STABILISING
  }

  enum class Type {
    INITIAL,
    ADDED,
    REMOVED,
    UPDATED
  }
}