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

import io.blockv.common.internal.json.serializer.Serializer

class ChildPolicy {
  @Serializer.Serialize
  var count: Int?
  @Serializer.Serialize(name = "template_variation")
  var templateVariation: String?
  @Serializer.Serialize(name = "creation_policy")
  var creationPolicy: CreationPolicy?

  @Serializer.Serializable
  constructor(
    count: Int?,
    templateVariation: String?,
    creationPolicy: CreationPolicy?
  ) {
    this.count = count
    this.templateVariation = templateVariation
    this.creationPolicy = creationPolicy
  }


  constructor(policy: ChildPolicy) : this(
    policy.count,
    policy.templateVariation,
    if (policy.creationPolicy != null) CreationPolicy(policy.creationPolicy!!) else null
  )

  override fun toString(): String {
    return "ChildPolicy{" +
      "count='" + count + '\'' +
      ", templateVariation='" + templateVariation + '\'' +
      "," + creationPolicy + '\'' +
      "}"
  }

}
