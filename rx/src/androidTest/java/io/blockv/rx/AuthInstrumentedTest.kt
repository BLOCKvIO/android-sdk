package io.blockv.rx

import android.support.test.runner.AndroidJUnit4
import io.blockv.core.client.builder.RegistrationBuilder
import io.blockv.core.client.manager.UserManager
import io.blockv.core.internal.net.rest.exception.BlockvException
import io.blockv.core.model.Error
import io.blockv.core.model.User
import io.blockv.rx.client.manager.UserManager.Companion.NULL_USER
import org.awaitility.Awaitility
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import kotlin.test.junit.JUnitAsserter.fail

@RunWith(AndroidJUnit4::class)
class AuthInstrumentedTest {

  @Before
  fun reset() {
    env.reset()
  }

  @Test(timeout = 10000)
  fun registerNewUserEmail() {
    try {
      var user: User? = null

      env.blockv.userManager.register(
        RegistrationBuilder()
          .addEmail(env.createRandomEmailToken("reg"))
          .setFirstName("Joe")
          .setLastName("Soap")
          .setBirthday("1990-01-01")
          .setPassword(env.password)
          .build()
      )
        .subscribe({
          user = it
        }, {
          fail(it.message)
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

      env.blockv.netModule.client.del("/v1/user")
    } catch (e: Exception) {
      fail(e.message)
    }
  }

  @Test(timeout = 10000)
  fun registerExistingUserEmail() {
    try {

      var error: Throwable? = null

      env.blockv.userManager.register(
        RegistrationBuilder()
          .addEmail(env.authEmail)
          .setFirstName("Joe")
          .setLastName("Soap")
          .setPassword(env.password)
          .build()
      )
        .subscribe({
          fail("Registration succeeded")
        }, {
          error = it
        })

      Awaitility.await().atMost(10000, TimeUnit.MILLISECONDS).until { error != null }

      if (error !is BlockvException) fail("Error is not a BlockvException ${error!!.message}")

      val blockvException: BlockvException = error as BlockvException

      if (blockvException.error != Error.TOKEN_UNAVAILABLE) fail("Error is not TOKEN_UNAVAILABLE $error")

    } catch (e: Exception) {
      fail(e.message)
    }
  }

  @Test(timeout = 10000)
  fun loginExistingUserEmail() {
    try {

      var user: User? = null

      env.blockv.userManager.login(
        env.authEmail,
        UserManager.TokenType.EMAIL,
        env.password
      ).subscribe({
        user = it
        if (user == NULL_USER) {
          fail("User is null")
        }
      }, { throw it })

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

  companion object {

    private val env: EnvironmentConfig = EnvironmentConfig()

    @BeforeClass
    @JvmStatic
    fun setup() {
    }

    @AfterClass
    @JvmStatic
    fun clean() {
      env.reset()
    }
  }
}