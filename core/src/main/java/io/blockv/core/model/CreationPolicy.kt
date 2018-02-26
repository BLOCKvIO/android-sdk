package io.blockv.core.model

/**
 * "creation_policy": {
 * "auto_create": "create_new",
 * "auto_create_count": 1,
 * "auto_create_count_random": false,
 * "enforce_policy_count_max": true,
 * "enforce_policy_count_min": false,
 * "policy_count_max": 1,
 * "policy_count_min": 0,
 * "weighted_choices": []
 * },
 */
class CreationPolicy(var autoCreate: String?,
                     var autoCreateCount: Int?,
                     var isAutoCreateCountRandom: Boolean?,
                     var isEnforcePolicyCountMax: Boolean?,
                     var isEnforcePolicyCountMin: Boolean?,
                     var policyCountMax: Int?,
                     var policyCountMin: Int?) {


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
