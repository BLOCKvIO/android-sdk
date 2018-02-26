package io.blockv.core.model

class VatomResource(var name: String,
                    var type: String,
                    var url: String) {

  override fun toString(): String {
    return "VatomResource{" +
      "name='" + name + '\'' +
      ", type='" + type + '\'' +
      ", url='" + url + '\'' +
      '}'
  }

  override fun equals(o: Any?): Boolean {
    if (this === o) return true
    if (o !is VatomResource) return false
    val resource = o
    if (name != resource.name) return false
    if (type != resource.type) return false
    return url == resource.url
  }

  override fun hashCode(): Int {
    return (name+type+url).hashCode()
  }

}
