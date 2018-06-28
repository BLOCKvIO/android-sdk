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

class ActivityMessage(val id: Long,
                      val userId: String,
                      val vatomIds: List<String>,
                      val templateVariationIds: List<String>,
                      val message: String,
                      val action: String,
                      val whenCreated: String,
                      val triggeredBy: String,
                      val resources: List<Resource>,
                      val geoPosition: List<Double>)