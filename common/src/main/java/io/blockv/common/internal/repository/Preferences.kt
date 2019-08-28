/*
 *  BlockV AG. Copyright (c) 2018, all rights reserved.
 *
 *  Licensed under the BlockV SDK License (the "License"); you may not use this file or the BlockV SDK except in
 *  compliance with the License accompanying it. Unless required by applicable law or agreed to in writing, the BlockV
 *  SDK distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 *  ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 *  under the License.
 *
 */
package io.blockv.common.internal.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import io.blockv.common.internal.json.JsonModule
import io.blockv.common.model.AssetProvider
import io.blockv.common.model.Environment
import io.blockv.common.model.Jwt
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Preferences(
  val context: Context,
  private val jsonModule: JsonModule
) {

  enum class Key {
    ENVIRONMENT,
    REFRESH_TOKEN,
    ASSET_PROVIDER,
    INVENTORY_HASH
  }

  private val preferences: SharedPreferences =
    context.getSharedPreferences("io.blockv.core.prefs", Context.MODE_PRIVATE)

  var environment: Environment?
    get() {
      val json = getString(Key.ENVIRONMENT, null)
      if (json != null) {
        try {
          val data = JSONObject(json)
          return jsonModule.deserialize(data)
        } catch (e: JSONException) {
          e.printStackTrace()
        }
      }
      return null
    }
    set(environment) {
      if (environment != null) {
        set(Key.ENVIRONMENT, jsonModule.serialize(environment).toString())
      }
    }

  var refreshToken: Jwt?
    get() {
      val token: String? = getString(Key.REFRESH_TOKEN)
      try {
        if (token != null && token.isNotEmpty()) {
          return jsonModule.deserialize(JSONObject(token))
        }
      } catch (error: Exception) {
        Log.e("preferences", error.toString())
      }
      return null
    }
    set(token) = set(Key.REFRESH_TOKEN, if (token != null) jsonModule.serialize(token).toString() else "")

  var assetProviders: List<AssetProvider>
    get() {
      val providers = ArrayList<AssetProvider>()
      val json = getString(Key.ASSET_PROVIDER, null)
      if (json != null) {
        try {
          val array = JSONArray(json)

          for (i in 0 until array.length()) {
            val provider = jsonModule.deserialize<AssetProvider>(array.getJSONObject(i))
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
        array.put(jsonModule.serialize(provider))
      }
      set(Key.ASSET_PROVIDER, array.toString())
    }

  var inventoryHash: String?
    get() {
      return getString(Key.INVENTORY_HASH, null)
    }
    set(value) {
      set(Key.INVENTORY_HASH, value ?: "")
    }

  fun getInt(key: Key): Int {
    return preferences.getInt(key.name, 0)
  }

  fun getInt(key: Key, value: Int): Int {
    return preferences.getInt(key.name, value)
  }

  fun getString(key: Key): String? {
    return preferences.getString(key.name, null)
  }

  fun getString(key: Key, value: String?): String? {
    return preferences.getString(key.name, value)
  }

  fun set(key: Key, value: Boolean) {
    preferences.edit().putBoolean(key.name, value).apply()
  }

  fun set(key: Key, value: String) {
    preferences.edit().putString(key.name, value).apply()
  }

  fun set(key: Key, value: Float) {
    preferences.edit().putFloat(key.name, value).apply()
  }

  fun set(key: Key, value: Int) {
    preferences.edit().putInt(key.name, value).apply()
  }

  fun set(key: Key, value: Long) {
    preferences.edit().putLong(key.name, value).apply()
  }

  fun set(key: Key, value: Set<String>) {
    preferences.edit().putStringSet(key.name, value).apply()
  }

  @SuppressLint("ApplySharedPref")
  fun clear() {
    preferences.edit().clear()
      .commit()//prefs are expected be cleared directly when function is called, called before sdk reset
  }


}
