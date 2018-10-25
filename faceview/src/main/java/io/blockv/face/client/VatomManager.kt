package io.blockv.face.client

import io.blockv.common.model.Vatom
import io.blockv.common.util.Callable

interface VatomManager {

  /**
   * Fetches vAtoms by id.
   *
   * @param ids is a list of vAtom id's in the current users inventory.
   * @return new Callable<List<Vatom>> instance.
   * @see Vatom
   */
  fun getVatoms(vararg ids: String): Callable<List<Vatom>>

  /**
   * Fetches the current user's inventory of vAtoms.
   *
   * @param id is the id of the inventory you want to fetch. If null or '.' supplied the
   *           user's root inventory will be returned.
   * @param page indicates which slice of the vAtom inventory is returned. If set as
   *             zero, the first page is returned.
   * @param limit defines the number of vAtoms per response page (up to 100). If omitted or set as
   *              zero, the max number is returned.
   * @return new Callable<List<Vatom>> instance.
   * @see Vatom
   */
  fun getInventory(id: String?, page: Int, limit: Int): Callable<List<Vatom>>


}