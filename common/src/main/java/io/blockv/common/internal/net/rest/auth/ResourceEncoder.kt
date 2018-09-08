package io.blockv.common.internal.net.rest.auth

interface ResourceEncoder {

  @Throws(MissingAssetProviderException::class)
  fun encodeUrl(url: String): String

  class MissingAssetProviderException : Exception("BLOCKv SDK does not have any asset provider credentials!")

}