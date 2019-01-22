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

class DiscoverPack : Model {
  @Serializer.Serialize
  val count: Int
  @Serializer.Serialize(name = "results")
  val vatoms: List<Vatom>
  @Serializer.Serialize(name = "faces")
  internal val faces: List<Face>
  @Serializer.Serialize(name = "actions")
  internal val actions: List<Action>

  @Serializer.Serializable
  constructor(
    count: Int,
    vatoms: List<Vatom>,
    faces: List<Face> = ArrayList(),
    actions: List<Action> = ArrayList()
  ) {
    this.count = count
    this.vatoms = vatoms
    this.faces = faces
    this.actions = actions
  }
}