package io.blockv.rx

import android.support.test.runner.AndroidJUnit4
import io.blockv.core.client.manager.UserManager
import io.blockv.core.model.Pack
import io.blockv.core.model.User
import io.blockv.rx.client.manager.UserManager.Companion.NULL_USER
import org.awaitility.Awaitility.await
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.test.junit.JUnitAsserter.fail


@RunWith(AndroidJUnit4::class)
class VatomInstrumentedTest {

  @Test(timeout = 10000)
  fun fetchInventoryRoot() {
    var pack: Pack? = null
    try {
      env.blockv.vatomManager.getInventory(".", 1, 100)
        .subscribe({
          pack = it
        }, {
          throw it
        })

      await().atMost(10000, TimeUnit.MILLISECONDS).until { pack != null }

      //size check
      if (pack!!.vatoms.size != 4) fail("vatom array is not of size 4 : size = " + pack!!.vatoms.size)
      if (pack!!.faces.size != 6) fail("face array is not of size 6 : size = " + pack!!.faces.size)
      if (pack!!.actions.isNotEmpty()) fail("action array is not of size 0 : size = " + pack!!.actions.size)

      //vatom template variation check
      val templateVariations: Array<String> = arrayOf(
        "vatomic.prototyping::unit-test-container::v1::fruit-juice::v1",
        "vatomic.prototyping::unit-test::v1::fruit-orange::v1",
        "vatomic.prototyping::unit-test::v1::fruit-lemon::v1",
        "vatomic.prototyping::unit-test::v1::ice::v1"
      )

      templateVariations.forEach {
        val tempVar = it
        if (pack!!.vatoms.none { it.property.templateVariationId == tempVar }) {
          fail("No vAtoms found with template variation = $tempVar")
        }
      }

      //face template check
      val templates: Array<String> = arrayOf(
        "vatomic.prototyping::unit-test-container::v1",
        "vatomic.prototyping::unit-test::v1"
      )
      val platform = arrayOf("ios", "android", "web")

      templates.forEach {
        val temp = it
        platform.forEach {
          val plat = it
          if (pack!!.faces.none { it.property.platform == plat && it.templateId == temp }) {
            fail("No faces found with template = $temp and platform = $plat")
          }
        }
      }
    } catch (e: Exception) {
      fail(e.message)
    }
  }

  @Test(timeout = 10000)
  fun fetchInventoryChild() {
    var pack: Pack? = null
    try {
      val vatomId: String =
        if (env.env == EnvironmentConfig.Environment.DEV) "df8d9f86-74db-44b0-8f76-a527a1265d8d" else "71cdced6-45d2-4b6c-8bcf-4b91f7d506f2"

      env.blockv.vatomManager.getInventory(vatomId, 1, 100)
        .subscribe({
          pack = it
        }, {
          throw it
        })
      await().atMost(10000, TimeUnit.MILLISECONDS).until { pack != null }
      //size check
      if (pack!!.vatoms.size != 4) fail("vatom array is not of size 4 : size = " + pack!!.vatoms.size)
      if (pack!!.faces.size != 3) fail("face array is not of size 3 : size = " + pack!!.faces.size)
      if (pack!!.actions.isNotEmpty()) fail("action array is not of size 0 : size = " + pack!!.actions.size)

      //vatom template variation check
      val templateVariations: Array<String> = arrayOf(
        "vatomic.prototyping::unit-test::v1::fruit-orange::v1",
        "vatomic.prototyping::unit-test::v1::fruit-lemon::v1",
        "vatomic.prototyping::unit-test::v1::ice::v1"
      )

      templateVariations.forEach {
        val tempVar = it
        if (pack!!.vatoms.none { it.property.templateVariationId == tempVar }) {
          fail("No vAtoms found with template variation = $tempVar")
        }
      }

      //face template check
      val templates: Array<String> = arrayOf(
        "vatomic.prototyping::unit-test::v1"
      )
      val platform = arrayOf("ios", "android", "web")

      templates.forEach {
        val temp = it
        platform.forEach {
          val plat = it
          if (pack!!.faces.none { it.property.platform == plat && it.templateId == temp }) {
            fail("No faces found with template = $temp and platform = $plat")
          }
        }
      }
    } catch (e: Exception) {
      fail(e.message)
    }
  }

  @Test(timeout = 10000)
  fun fetchSingleVatom() {
    val validDate = { format: String, value: String ->
      {
        var date: Date? = null
        try {
          val sdf = SimpleDateFormat(format)
          date = sdf.parse(value)
          if (value != sdf.format(date)) {
            date = null
          }
        } catch (ex: ParseException) {
        }
        date != null
      }
    }

    var pack: Pack? = null
    try {
      val vatomId: String =
        if (env.env == EnvironmentConfig.Environment.DEV) "df8d9f86-74db-44b0-8f76-a527a1265d8d" else "71cdced6-45d2-4b6c-8bcf-4b91f7d506f2"
      env.blockv.vatomManager.getVatoms(vatomId)
        .subscribe({
          pack = it
        }, {
          throw it
        })
      await().atMost(10000, TimeUnit.MILLISECONDS).until { pack != null }
      //size check
      if (pack!!.vatoms.size != 1) fail("vatom array is not of size 1 : size = " + pack!!.vatoms.size)
      if (pack!!.faces.size != 3) fail("face array is not of size 3 : size = " + pack!!.faces.size)
      if (pack!!.actions.isNotEmpty()) fail("action array is not of size 0 : size = " + pack!!.actions.size)

      val vatom = pack!!.vatoms[0]
      //vatom validation
      if (vatom.id != vatomId) fail("vAtom id does not match ${vatom.id}")
      if (!validDate.invoke(
          "yyyy-MM-dd'T'HH:mm:ss",
          vatom.whenCreated.removeSuffix("Z")
        ).invoke()
      ) fail("when_created not in correct format yyyy-MM-dd'T'HH:mm:ssZ = ${vatom.whenCreated}")
      if (!validDate.invoke(
          "yyyy-MM-dd'T'HH:mm:ss",
          vatom.whenModified.removeSuffix("Z")
        ).invoke()
      ) fail("when_modified not in correct format yyyy-MM-dd'T'HH:mm:ssZ = ${vatom.whenModified}")
      if (vatom.private == null) fail("private properties should be empty not null ")
      if (vatom.private!!.length() > 0) fail("private properties should be empty")
      //vatom property validation
      if (vatom.property.parentId != ".") fail("parent id != '.' : ${vatom.property.parentId}")
      if (vatom.property.publisherFqdn != "vatomic.prototyping") fail("publisher fqdn != 'vatomic.prototyping' : ${vatom.property.publisherFqdn}")
      if (vatom.property.rootType != "vAtom::vAtomType::DefinedFolderContainerType") fail("Root != 'vAtom::vAtomType::DefinedFolderContainerType' : ${vatom.property.rootType}")
      if (vatom.property.owner != user!!.id) fail("Owner != ${user!!.id} : ${vatom.property.rootType}")
      if (vatom.property.author == null || vatom.property.author!!.isEmpty()) fail("Invalid Auhtor : ${vatom.property.author}")
      if (vatom.property.templateId != "vatomic.prototyping::unit-test-container::v1") fail("TemplateId != 'vatomic.prototyping::unit-test-container::v1' : ${vatom.property.templateId}")
      if (vatom.property.templateVariationId != "vatomic.prototyping::unit-test-container::v1::fruit-juice::v1") fail("templateVariationId != 'vatomic.prototyping::unit-test-container::v1::fruit-juice::v1' : ${vatom.property.templateVariationId}")
      if (vatom.property.notifyMsg != "") fail("notify_msg != '' : ${vatom.property.notifyMsg}")
      if (vatom.property.title != "Fruit Juice") fail("Title != 'Fruit Juice' : ${vatom.property.title}")
      if (vatom.property.description != "A vAtom to be used with unit tests") fail("Description != 'A vAtom to be used with unit tests' : ${vatom.property.title}")
      if (vatom.property.category != "Food & Drink") fail("category != 'Food & Drink' : ${vatom.property.category}")
      if (vatom.property.tags == null || vatom.property.tags!!.isNotEmpty()) fail("Tags is invalid: ${vatom.property.tags}")
      if (!vatom.property.isTransferable) fail("isTransferable != true: ${vatom.property.isTransferable}")
      if (vatom.property.isAcquireable) fail("isAcquireable == true: ${vatom.property.isAcquireable}")
      if (vatom.property.isTradeable) fail("isTradeable == true: ${vatom.property.isTradeable}")
      if (vatom.property.transferredBy == null || vatom.property.transferredBy!!.isEmpty()) fail("Invalid transffered by : ${vatom.property.transferredBy}")
      if (vatom.property.clonedFrom == null || vatom.property.clonedFrom!!.isNotEmpty()) fail("Invalid clonedFrom : ${vatom.property.clonedFrom}")
      if (vatom.property.cloningScore == null || vatom.property.cloningScore!! != 0f) fail("Invalid cloningScore : ${vatom.property.cloningScore}")
      if (vatom.property.isInContract) fail("isInContract == true: ${vatom.property.isInContract}")
      if (vatom.property.isRedeemable) fail("isRedeemable == true: ${vatom.property.isRedeemable}")
      if (vatom.property.inContractWith == null || vatom.property.inContractWith!!.isNotEmpty()) fail("Invalid inContractWith : ${vatom.property.inContractWith}")
      if (vatom.property.numDirectClones == null || vatom.property.numDirectClones!! != 0) fail("Invalid numDirectClones : ${vatom.property.numDirectClones}")
      if (vatom.property.isDropped) fail("isDropped == true: ${vatom.property.isDropped}")
      if (vatom.property.commerce == null) fail("Invalid commerce: ${vatom.property.commerce}")
      // if (vatom.property.states==null) fail("Invalid commerce: ${vatom.property.commerce}")
      if (vatom.property.visibility == null) fail("Invalid visibility: ${vatom.property.visibility}")
      if (vatom.property.geoPos == null) fail("Invalid geoPos: ${vatom.property.geoPos}")
      if (vatom.property.childPolicy == null) fail("Invalid childPolicy: ${vatom.property.childPolicy}")
      //commerce validation
      if (vatom.property.commerce!!.pricing == null) fail("Invalid commerce pricing: ${vatom.property.commerce!!.pricing}")
      if (vatom.property.commerce!!.pricing!!.priceType?.isEmpty() != true) fail("Invalid commerce priceType: ${vatom.property.commerce!!.pricing!!.priceType}")
      if (vatom.property.commerce!!.pricing!!.currency?.isEmpty() != true) fail("Invalid commerce currency: ${vatom.property.commerce!!.pricing!!.currency}")
      if (vatom.property.commerce!!.pricing!!.validFrom?.isEmpty() != true) fail("Invalid commerce validFrom: ${vatom.property.commerce!!.pricing!!.validFrom}")
      if (vatom.property.commerce!!.pricing!!.validThrough?.isEmpty() != true) fail("Invalid commerce validThrough: ${vatom.property.commerce!!.pricing!!.validThrough}")
      if (vatom.property.commerce!!.pricing!!.isVatIncluded) fail("Invalid commerce isVatIncluded: ${vatom.property.commerce!!.pricing!!.isVatIncluded}")
      //resource validation
      if (vatom.property.resources.size != 1) fail("Resources size != 1: ${vatom.property.resources.size}")
      if (vatom.property.resources[0].name != "ActivatedImage") fail("Resource name != ActivatedImage: ${vatom.property.resources[0].name}")
      if (vatom.property.resources[0].type != "ResourceType::Image::PNG") fail("Resource type != ResourceType::Image::PNG : ${vatom.property.resources[0].type}")
      if (!vatom.property.resources[0].url.endsWith("/blockv/publisher/vatomic.prototyping/unit-test/fruit-juice.png")) fail(
        "Resource url invalid: ${vatom.property.resources[0].url}"
      )
      //visibility validation
      if (vatom.property.visibility!!.type != "owner") fail("visibility type != owner: ${vatom.property.visibility!!.type}")
      if (vatom.property.visibility!!.value != "*") fail("visibility value != *: ${vatom.property.visibility!!.value}")
      //geo_pos validation
      if (vatom.property.geoPos!!.reqType != "GEOMETRY") fail("geoPos reqType != GEOMETRY : ${vatom.property.geoPos!!.reqType}")
      if (vatom.property.geoPos!!.coordinates == null
        || vatom.property.geoPos!!.coordinates!!.size != 2
        || vatom.property.geoPos!!.coordinates!![0] != 0f
        || vatom.property.geoPos!!.coordinates!![1] != 0f
      ) fail("invalid geoPos coordinates : ${vatom.property.geoPos!!.coordinates}")
      if (vatom.property.geoPos!!.type != "Point") fail("geoPos type != Point : ${vatom.property.geoPos!!.type}")

      //child policy validation
      if (vatom.property.childPolicy!!.size != 3) fail("childPolicy size != 3: ${vatom.property.childPolicy!!.size}")
      if (vatom.property.childPolicy!![0].templateVariation != "vatomic.prototyping::unit-test::v1::fruit-orange::v1") fail(
        "childPolicy templateVariation != 'vatomic.prototyping::unit-test::v1::fruit-orange::v1': ${vatom.property.childPolicy!![0].templateVariation}"
      )
      if (vatom.property.childPolicy!![0].creationPolicy == null) fail("childPolicy creationPolicy is null : ${vatom.property.childPolicy!![0].creationPolicy}")
      if (vatom.property.childPolicy!![0].creationPolicy!!.autoCreate != "create_new") fail("childPolicy autoCreate != 'create_new' : ${vatom.property.childPolicy!![0].creationPolicy!!.autoCreate}")
      if (vatom.property.childPolicy!![0].creationPolicy!!.autoCreateCount != 1) fail("childPolicy autoCreateCount != 1 : ${vatom.property.childPolicy!![0].creationPolicy!!.autoCreateCount}")
      if (vatom.property.childPolicy!![0].creationPolicy!!.isAutoCreateCountRandom) fail("childPolicy isAutoCreateCountRandom != false : ${vatom.property.childPolicy!![0].creationPolicy!!.isAutoCreateCountRandom}")
      if (vatom.property.childPolicy!![0].creationPolicy!!.policyCountMin != 0) fail("childPolicy policyCountMin != 0 : ${vatom.property.childPolicy!![0].creationPolicy!!.policyCountMin}")
      if (vatom.property.childPolicy!![0].creationPolicy!!.policyCountMax != 1) fail("childPolicy policyCountMax != 1 : ${vatom.property.childPolicy!![0].creationPolicy!!.policyCountMax}")
      if (!vatom.property.childPolicy!![0].creationPolicy!!.isEnforcePolicyCountMax) fail("childPolicy isEnforcePolicyCountMax != true : ${vatom.property.childPolicy!![0].creationPolicy!!.isEnforcePolicyCountMax}")
      if (vatom.property.childPolicy!![0].creationPolicy!!.isEnforcePolicyCountMin) fail("childPolicy isEnforcePolicyCountMin != false : ${vatom.property.childPolicy!![0].creationPolicy!!.isEnforcePolicyCountMin}")
      if (vatom.property.childPolicy!![0].count != 0) fail("childPolicy count != 0 : ${vatom.property.childPolicy!![0].count}")

    } catch (e: Exception) {
      fail(e.message)
    }


  }

  companion object {

    private val env: EnvironmentConfig = EnvironmentConfig()
    private lateinit var user: User

    @BeforeClass
    @JvmStatic
    fun setup() {
      var login = false
      env.blockv.userManager.login(
        env.vatomEmail,
        UserManager.TokenType.EMAIL,
        env.password
      ).subscribe({
        user = it!!
        if (user == NULL_USER) {
          throw Exception("User is null")
        }
        login = true
      }, {
        throw it
      })
      await().atMost(10000, TimeUnit.MILLISECONDS).until { login }
    }

    @AfterClass
    @JvmStatic
    fun clean() {
      env.reset()
    }
  }

}