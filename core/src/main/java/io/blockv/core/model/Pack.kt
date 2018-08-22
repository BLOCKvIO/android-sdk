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

  /**
   * Finds the vAtom with the specified id.
   *
   * @param vatomId is the unique identifier of the vAtom.
   * @return The first Vatom model of the sequence that satisfies the id predicate, or `null` if there is no
   * Vatom model matching the predicate.
   */
  fun findVatom(vatomId: String): Vatom? {
    return vatoms.find { it.id == vatomId }
  }

  /**
   * Returns the faces associated with the vAtom's template.
   *
   * @param vatomId is the unique identifier of the vAtom.
   * @return new List<Face> instance.
   */
  fun filterFaces(vatomId: String): List<Face> {
    val vatom = findVatom(vatomId)
    if (vatom != null) {
      return faces.filter { it.templateId == vatom.property.templateId }
    }
    return ArrayList()
  }

  /**
   * Returns the actions associated with the vAtom's template.
   *
   * @param vatomId is the unique identifier of the vAtom.
   * @return new List<Action> instance.
   */
  fun filterActions(vatomId: String): List<Action> {
    val vatom = findVatom(vatomId)
    if (vatom != null) {
      return actions.filter { it.templateId == vatom.property.templateId }
    }
    return ArrayList()
  }

  /**
   * Returns a VatomPack model for the specified vAtom id.
   *
   * @param vatomId is the unique identifier of the vAtom.
   * @return new VatomPack instance.
   */
  fun filter(vatomId: String): VatomPack? {
    val vatom = findVatom(vatomId)
    val faces = filterFaces(vatomId)
    val actions = filterActions(vatomId)
    val vatoms = ArrayList<Vatom>()
    if (vatom != null) {
      vatoms.add(vatom)
    }
    if (vatoms.size == 0)
      return null

    return VatomPack(vatoms[0], faces, actions)
  }

  override fun toString(): String {
    return "Pack{" +
      " vAtoms='" + vatoms + '\'' +
      ", faces='" + faces + '\'' +
      ", actions='" + actions + '\'' +
      "}"
  }
}
