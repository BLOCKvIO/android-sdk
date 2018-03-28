package io.blockv.core.client.manager

import io.blockv.core.model.AssetProvider

/**
 * Created by LordCheddar on 2018/03/09.
 */

interface ResourceManager {

  val assetProviders: List<AssetProvider>?

  fun encodeUrl(url: String?): String?
}
