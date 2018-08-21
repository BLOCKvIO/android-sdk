package io.blockv.face.client

class FaceManagerImpl : FaceManager {

  private val factories: HashMap<String, FaceViewFactory> = HashMap()

  override fun registerFaceViewFactory(viewFactory: FaceViewFactory) {
    factories[viewFactory.displayUrl] = viewFactory
  }

  override fun getFaceViewFactory(registryId: String): FaceViewFactory? {
    return factories[registryId]
  }

  override val faceRegistry: Set<String>
    get() = factories.keys
}