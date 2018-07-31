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
package io.blockv.core.model

open class Pack(
  val vatoms: List<Vatom>,
  val faces: List<Face>,
  val actions: List<Action>
) {

  fun getVatom(vatomId: String): Vatom? {
    return vatoms.find { it.id == vatomId }
  }

  fun getFaces(vatomId: String): List<Face> {
    val vatom = getVatom(vatomId)
    if (vatom != null) {
      return faces.filter { it.templateId == vatom.property.templateId }
    }
    return ArrayList()
  }

  fun getActions(vatomId: String): List<Action> {
    val vatom = getVatom(vatomId)
    if (vatom != null) {
      return actions.filter { it.templateId == vatom.property.templateId }
    }
    return ArrayList()
  }

  fun filterByVatom(vatomId: String): Pack {
    val vatom = getVatom(vatomId)
    val faces = getFaces(vatomId)
    val actions = getActions(vatomId)
    val vatoms = ArrayList<Vatom>()
    if (vatom != null) {
      vatoms.add(vatom)
    }
    return Pack(vatoms, faces, actions)
  }

  override fun toString(): String {
    return "Pack{" +
      " vAtoms='" + vatoms + '\'' +
      ", faces='" + faces + '\'' +
      ", actions='" + actions + '\'' +
      "}"
  }
}
