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

class Registration(
  var firstName: String?,
  var lastName: String?,
  var birthday: String?,
  var avatarUri: String?,
  var password: String?,
  var language: String?,
  var tokens: List<Token>?
) {
  open class Token(val type: String, val value: String)

  class OauthToken(type: String, value: String, val auth: String) : Token(type, value)

}