package io.blockv.core

import android.support.test.runner.AndroidJUnit4
import io.blockv.common.builder.RegistrationBuilder
import io.blockv.core.client.manager.UserManager
import io.blockv.common.model.Token
import io.blockv.common.model.User
import io.blockv.common.util.Callable
import org.awaitility.Awaitility
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import kotlin.test.junit.JUnitAsserter.fail

@RunWith(AndroidJUnit4::class)
class UserInstrumentedTest {

  @Test(timeout = 10000)
  fun getCurrentUserProfile() {
    try {
      var user: User? = null

      env.blockv.userManager.getCurrentUser()
        .call({
          user = it
          if (user == null) {
            fail("User is null")
          }
        },
          {
            throw it
          })

      Awaitility.await().atMost(10000, TimeUnit.MILLISECONDS).until { user != null }
      if (user!!.id.isEmpty()) fail("User id is empty")
      if (user!!.firstName != "Joe") fail("First name != 'Joe' ${user!!.firstName}")
      if (user!!.lastName != "Soap") fail("Last name != 'Soap' ${user!!.lastName}")
      if (user!!.birthday != "1990-01-01") fail("Birthday != '1990-01-01' ${user!!.birthday}")
      if (user!!.language != "en") fail("Language != 'en' ${user!!.language}")
      if (!user!!.isAvatarPublic) fail("Avatar is not public")
      if (!user!!.isNamePublic) fail("Name is not public")
      if (user!!.nonPushNotifications) fail("nonPushNotifications is not enabled")

    } catch (e: Exception) {
      fail(e.message)
    }
  }

  @Test(timeout = 10000)
  fun addUserToken() {
    try {
      var complete = false
      addRandomToken()
        .call {
          complete = true
        }
      Awaitility.await().atMost(10000, TimeUnit.MILLISECONDS).until { complete }
    } catch (e: Exception) {
      fail(e.message)
    }
  }

  @Test(timeout = 10000)
  fun deleteUserToken() {
    try {
      var tokenId: String? = null

      addRandomToken().call {
        tokenId = it!!.id
      }
      Awaitility.await().atMost(10000, TimeUnit.MILLISECONDS).until { tokenId != null }

      var complete = false

      env.blockv.userManager.deleteCurrentUserToken(tokenId!!)
        .call {
          complete = true
        }

      Awaitility.await().atMost(10000, TimeUnit.MILLISECONDS).until { complete }
    } catch (e: Exception) {
      fail(e.toString())
    }
  }

  companion object {

    private val env: EnvironmentConfig = EnvironmentConfig()

    @BeforeClass
    @JvmStatic
    fun setup() {
      var user: User? = null
      env.blockv.userManager.register(
        RegistrationBuilder()
          .addEmail(env.createRandomEmailToken("user"))
          .setFirstName("Joe")
          .setLastName("Soap")
          .setBirthday("1990-01-01")
          .setPassword(env.password)
          .build()
      ).call {
        user = it
        if (user == null)
          throw Exception("user is null")
      }
      Awaitility.await().atMost(10000, TimeUnit.MILLISECONDS).until { user != null }
    }

    @AfterClass
    @JvmStatic
    fun clean() {
      env.blockv.netModule.client.del("/v1/user")
      env.reset()
    }

    fun addRandomToken(): Callable<Token?> {
      return env.blockv.userManager.addCurrentUserToken(
        env.createRandomEmailToken("user"),
        UserManager.TokenType.EMAIL,
        false
      )
    }
  }
}