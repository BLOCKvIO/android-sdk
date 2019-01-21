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
package io.blockv.face.client

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.blockv.common.model.Face
import io.blockv.common.model.Vatom
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Base FaceView class that must be extended to create custom FaceViews.
 */
abstract class FaceView(
  var vatom: Vatom,
  var face: Face,
  val bridge: FaceBridge
) {
  private var disposable: CompositeDisposable = CompositeDisposable()

  @get:Synchronized
  @set:Synchronized
  var isLoaded: Boolean = false

  /**
   * Creates a view for this FaceView. The view must not be attached to the container.
   *
   * This is is the starting point of the FaceView life cycle. This is only called once.
   *
   * @param inflater is a LayoutInflater to be used to inflate your view's layout file.
   * @param container is the ViewGroup which the returned view will be attached to.
   * @return a new View for this FaceView.
   */
  abstract fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View

  /**
   * All logic needed to display this FaceView should be done here.
   *
   * This is called after after onCreateView and optionally after onVatomChanged.
   *
   * After the load completes or fails you are required to call the corresponding function on the handler.
   *
   * All long running actions should be added to handler.collect() function so they can be canceled in the case
   * that the FaceView load is canceled.
   *
   * @param handler provides function to indicate when loading of FaceView is complete.
   * @see LoadHandler
   * @see LoadHandler.collect
   * @see onVatomChanged
   */
  abstract fun onLoad(handler: LoadHandler)

  fun update(vatom: Vatom, handler: LoadHandler) {
    val temp = this.vatom
    onVatomChanged(vatom)
    this.vatom = vatom
    if (onVatomChanged(temp, vatom)) {
      onLoad(handler)
    } else
      handler.onComplete()
  }

  /**
   * Called when the backing vAtom is about to change. This can happen at any time during the FaceView
   * life cycle.
   *
   * A new vAtom can be assigned, only the vAtom's template variation id is guaranteed to be the same.
   *
   * @param vatom is the new Vatom model containing the updates.
   */
  open fun onVatomChanged(vatom: Vatom) {}

  /**
   * Called when the backing vAtom has changed and returns a Boolean to indicate if onLoad should be called again.
   * This can happen at any time during the FaceView life cycle.
   *
   * A new vAtom can be assigned, only the vAtom's template variation id is guaranteed to be the same.
   *
   * @param oldVatom is the original Vatom model before the update.
   * @param newVatom is the new Vatom model containing the updates.
   * @return a Boolean indicating if onLoad must be run again.
   *
   * @see Vatom
   * @see onLoad
   */
  open fun onVatomChanged(oldVatom: Vatom, newVatom: Vatom): Boolean {
    return true
  }

  /**
   * Called when the FaceView has been removed from it's containing VatomView
   * or when the VatomView itself has been de attached from the window.
   *
   * All long running actions not part of onLoad should be canceled here.
   */
  open fun onUnload() {
    cancel()
  }

  /**
   * Helper function that collects Cancellables to be canceled on unload.
   * This should be used to help clean up long running actions to prevent memory leaks.
   *
   * @param disposable to be disposed on unload.
   *
   * @see onUnload
   */
  @Synchronized
  fun collect(disposable: Disposable) {
    this.disposable.add(disposable)
  }

  /**
   * Helper function to cancel all the collected Cancellables.
   *
   * @see collect
   */
  @Synchronized
  fun cancel() {
    disposable.dispose()
    disposable = CompositeDisposable()
  }

  /**
   * Handler to be used by onLoad.
   * @see onLoad
   */
  interface LoadHandler {

    /**
     * This must be called once onLoad has completed successfully.
     *
     * This will cause the FaceView to be displayed.
     */
    fun onComplete()

    /**
     * This must be called if an error has occurred during onLoad.
     *
     * This will cause the error view to be displayed.
     *
     * @param error is the throwable to be returned to the loading chain.
     */
    fun onError(error: Throwable)

    /**
     * This should be used to collect all long running methods inside onLoad.
     * The loading chain can be canceled, triggering the collected disposables to be disposed.
     *
     * @param disposable to be disposed if load is canceled.
     */
    fun collect(disposable: Disposable)
  }

}