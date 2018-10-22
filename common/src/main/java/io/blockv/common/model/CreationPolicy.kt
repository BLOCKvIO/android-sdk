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
package io.blockv.common.model

import io.blockv.common.internal.json.JsonModule

class CreationPolicy {

  @JsonModule.Serialize(name = "auto_create")
  var autoCreate: String
  @JsonModule.Serialize(name = "auto_create_count")
  var autoCreateCount: Int
  @JsonModule.Serialize(name = "auto_create_count_random")
  var isAutoCreateCountRandom: Boolean
  @JsonModule.Serialize(name = "enforce_policy_count_max")
  var isEnforcePolicyCountMax: Boolean
  @JsonModule.Serialize(name = "enforce_policy_count_min")
  var isEnforcePolicyCountMin: Boolean
  @JsonModule.Serialize(name = "policy_count_max")
  var policyCountMax: Int
  @JsonModule.Serialize(name = "policy_count_min")
  var policyCountMin: Int

  @JsonModule.Serializable
  constructor(
    autoCreate: String,
    autoCreateCount: Int,
    isAutoCreateCountRandom: Boolean,
    isEnforcePolicyCountMax: Boolean,
    isEnforcePolicyCountMin: Boolean,
    policyCountMax: Int,
    policyCountMin: Int
  ) {
    this.autoCreate = autoCreate
    this.autoCreateCount = autoCreateCount
    this.isAutoCreateCountRandom = isAutoCreateCountRandom
    this.isEnforcePolicyCountMax = isEnforcePolicyCountMax
    this.isEnforcePolicyCountMin = isEnforcePolicyCountMin
    this.policyCountMax = policyCountMax
    this.policyCountMin = policyCountMin
  }

  override fun toString(): String {
    return "CreationPolicy{" +
      "autoCreate='" + autoCreate + '\'' +
      ", autoCreateCount='" + autoCreateCount + '\'' +
      ", isAutoCreateCountRandom='" + isAutoCreateCountRandom + '\'' +
      ", isEnforcePolicyCountMax='" + isEnforcePolicyCountMax + '\'' +
      ", isEnforcePolicyCountMin='" + isEnforcePolicyCountMin + '\'' +
      ", policyCountMax='" + policyCountMax + '\'' +
      ", policyCountMin='" + policyCountMin + '\'' +
      "}"
  }
}
