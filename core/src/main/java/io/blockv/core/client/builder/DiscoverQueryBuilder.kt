package io.blockv.core.client.builder

/**
 * Created by LordCheddar on 2018/02/28.
 */
class DiscoverQueryBuilder {

  enum class Scope {
    OWNER,
    TEMPLATE,
    TEMPLATE_VARIATION,
    ACQUIRABLE,
    PARENT_ID
  }

  enum class Filter {
    ID,
    PARENT_ID,
    WHEN_CREATED,
    WHEN_MODIFIED,
    OWNER,
    TEMPLATE,
    TEMPLATE_VARIATION,
    PUBLISHER,
    TITLE,
    TRANSFERABLE,
    TRANSFERED_BY,
    TRADABLE,
    DROPPED,
    ACQUIRABLE,
    AUTHOR,
    CATEGORY
  }



  fun setScope(key: String, value: String): DiscoverQueryBuilder {
    return this
  }

  fun addFilter(key: String, value: String): DiscoverQueryBuilder {
    return this
  }


}