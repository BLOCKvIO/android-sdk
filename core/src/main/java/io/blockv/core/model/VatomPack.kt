package io.blockv.core.model

class VatomPack(
  val vatom: Vatom,
  val faces: List<Face>,
  val actions: List<Action>
) {

  override fun toString(): String {
    return "VatomPack{" +
      " vAtoms='" + vatom + '\'' +
      ", faces='" + faces + '\'' +
      ", actions='" + actions + '\'' +
      "}"
  }
}