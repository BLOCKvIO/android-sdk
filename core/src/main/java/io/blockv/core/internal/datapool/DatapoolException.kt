package io.blockv.core.internal.datapool

class DatapoolException(val error: Error) : Exception(error.message) {
  enum class Error(val message: String) {
    REGION_DISPOSED("Datapool region has been disposed");

    fun exception(): DatapoolException {
      return DatapoolException(this)
    }
  }
}