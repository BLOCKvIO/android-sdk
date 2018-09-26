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
package io.blockv.common.util

class CompositeCancellable : ArrayList<Cancellable>(), Cancellable {
  @Synchronized
  override fun isComplete(): Boolean {
    forEach {
      if (it != null &&!it.isComplete()) {
        return false
      }
    }
    return true
  }

  @Synchronized
  override fun isCanceled(): Boolean {
    forEach {
      if (it != null &&!it.isCanceled()) {
        return false
      }
    }
    return true
  }

  @Synchronized
  override fun cancel() {
    forEach {
      if (it != null && !it.isCanceled() && !it.isComplete()) {
        it.cancel()
      }
    }
    clear()
  }
}