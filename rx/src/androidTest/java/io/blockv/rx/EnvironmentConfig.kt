package io.blockv.rx

import android.content.Context
import android.support.test.InstrumentationRegistry
import io.blockv.core.model.Environment
import io.blockv.rx.client.manager.Blockv
import java.util.*

class EnvironmentConfig {
  enum class Environment {
    PROD,
    DEV;
  }

  val context: Context
  val appId: String
  val blockv: Blockv
  val env: Environment
  val password: String
  val authEmail: String
  val vatomEmail: String
  val userEmail: String

  init {
    context = InstrumentationRegistry.getTargetContext()
    appId = InstrumentationRegistry.getArguments().getString("app_id") ?: ""
    authEmail = InstrumentationRegistry.getArguments().getString("ut_auth_email") ?: ""
    vatomEmail = InstrumentationRegistry.getArguments().getString("ut_vatom_email") ?: ""
    userEmail = InstrumentationRegistry.getArguments().getString("ut_user_email") ?: ""
    password = InstrumentationRegistry.getArguments().getString("ut_password") ?: ""
    val env = InstrumentationRegistry.getArguments().getString("env") ?: "prod"
    if (env == "dev") {
      this.env = Environment.DEV
      blockv = Blockv(
        context, Environment(
          "https://apidev.blockv.net/",
          "wss://ws.blockv.net/ws",
          appId
        )
      )
    } else {
      this.env = Environment.PROD
      blockv = Blockv(
        context, Environment(
          "https://api.blockv.io/",
          "wss://newws.blockv.io/ws",
          appId
        )
      )
    }
    reset()
  }

  fun createRandomEmailToken(testName: String): String {
    val random = Random(System.currentTimeMillis())
    return "ydangle.test+${testName}_rand_android_${random.nextInt(1000000)}@gmail.com"
  }

  fun reset() {
    blockv.userManager.logout().subscribe({}, {})
  }
}
