package io.blockv.face.client

class FaceManagerImpl : FaceManager {

  private val factories: HashMap<String, ViewFactory> = HashMap()

  override fun registerFace(factory: ViewFactory) {
    factories[factory.displayUrl] = factory
  }

  override val faceRegistry: Map<String,ViewFactory>
    get() = factories
}