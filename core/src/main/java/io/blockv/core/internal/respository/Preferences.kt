package io.blockv.android.core.internal.repository

import android.content.Context
import android.content.SharedPreferences
import io.blockv.core.internal.json.JsonModule
import io.blockv.core.model.AssetProvider
import io.blockv.core.model.Environment
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Preferences(context: Context,
                  private val jsonModule: JsonModule) {

  enum class Key {
    ENVIRONMENT,
    REFRESH_TOKEN,
    ASSET_PROVIDER
  }

  private val preferences: SharedPreferences = context.getSharedPreferences("com.blockv.core.prefs", Context.MODE_PRIVATE)

  private val context: Context = context.applicationContext

  var environment: Environment?
    get() {
      val json = getString(Key.ENVIRONMENT, null)
      if (json != null) {
        try {
          val data = JSONObject(json)
          return jsonModule.environmentDeserializer.deserialize(data)
        } catch (e: JSONException) {
          e.printStackTrace()
        }
      }
      return null
    }
    set(environment) {
      if (environment != null) {
        set(Key.ENVIRONMENT, jsonModule.environmentSerializer.serialize(environment).toString())
      }
    }

  var refreshToken: String
    get() = getString(Key.REFRESH_TOKEN)
    set(token) = set(Key.REFRESH_TOKEN, token)

  var assetProviders: List<AssetProvider>
    get() {
      val providers = ArrayList<AssetProvider>()
      val json = getString(Key.ASSET_PROVIDER, null)
      if (json != null) {
        try {
          val array = JSONArray(json)

          for (i in 0..array.length() - 1) {
            val provider = jsonModule.assetProviderDeserializer.deserialize(array.getJSONObject(i))
            if (provider != null) {
              providers.add(provider)
            }
          }
          return providers

        } catch (e: JSONException) {
          e.printStackTrace()
        }
      }
      return providers
    }
    set(assetProviders) {
      val array = JSONArray()
      for (provider in assetProviders) {
        array.put(jsonModule.assetProviderSerializer.serialize(provider))
      }
      set(Key.ASSET_PROVIDER, array.toString())
    }

  internal fun getInt(key: Key): Int {
    return preferences.getInt(key.name, 0)
  }

  internal fun getInt(key: Key, value: Int): Int {
    return preferences.getInt(key.name, value)
  }

  internal fun getString(key: Key): String {
    return preferences.getString(key.name, null)
  }

  internal fun getString(key: Key, value: String?): String? {
    return preferences.getString(key.name, value)
  }

  internal operator fun set(key: Key, value: Boolean) {
    preferences.edit().putBoolean(key.name, value).apply()
  }

  internal operator fun set(key: Key, value: String) {
    preferences.edit().putString(key.name, value).apply()
  }

  internal operator fun set(key: Key, value: Float) {
    preferences.edit().putFloat(key.name, value).apply()
  }

  internal operator fun set(key: Key, value: Int) {
    preferences.edit().putInt(key.name, value).apply()
  }

  internal operator fun set(key: Key, value: Long) {
    preferences.edit().putLong(key.name, value).apply()
  }

  internal operator fun set(key: Key, value: Set<String>) {
    preferences.edit().putStringSet(key.name, value).apply()
  }

  fun clear() {

    preferences.edit().clear().commit()
  }


}
