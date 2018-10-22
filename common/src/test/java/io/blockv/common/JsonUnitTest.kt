package io.blockv.common

import io.blockv.common.internal.json.JsonModule
import io.blockv.common.model.Vatom
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test


class JsonUnitTest {

  val vatom = "{\n" +
    "                \"id\": \"65a54bee-17ca-4dca-a779-xxxx\",\n" +
    "                \"when_created\": \"2018-10-15T12:53:56Z\",\n" +
    "                \"when_modified\": \"2018-10-15T12:54:10Z\",\n" +
    "                \"vAtom::vAtomType\": {\n" +
    "                    \"parent_id\": \"23d62263-f677-47de-9804-xxxx\",\n" +
    "                    \"publisher_fqdn\": \"vatomic.prototyping\",\n" +
    "                    \"root_type\": \"vAtom::vAtomType\",\n" +
    "                    \"owner\": \"b9e6581c-bb70-48d1-85eb-xxxx\",\n" +
    "                    \"author\": \"2e1038f8-ffcd-4e91-aa81-xxxx\",\n" +
    "                    \"template\": \"vatomic.prototyping::potatohead-part::v1\",\n" +
    "                    \"template_variation\": \"vatomic.prototyping::potatohead-part::v1::eyes::v1\",\n" +
    "                    \"notify_msg\": \"\",\n" +
    "                    \"title\": \"Mr Potato Head - Eyes\",\n" +
    "                    \"description\": \"Mr Potato Head's eyes\",\n" +
    "                    \"disabled\": false,\n" +
    "                    \"category\": \"Combinable\",\n" +
    "                    \"tags\": [],\n" +
    "                    \"transferable\": true,\n" +
    "                    \"acquirable\": false,\n" +
    "                    \"tradeable\": false,\n" +
    "                    \"transferred_by\": \"b9e6581c-bb70-48d1-85eb-xxxx\",\n" +
    "                    \"cloned_from\": \"\",\n" +
    "                    \"cloning_score\": 0,\n" +
    "                    \"in_contract\": false,\n" +
    "                    \"redeemable\": false,\n" +
    "                    \"in_contract_with\": \"\",\n" +
    "                    \"commerce\": {\n" +
    "                        \"pricing\": {\n" +
    "                            \"pricingType\": \"\",\n" +
    "                            \"value\": {\n" +
    "                                \"currency\": \"\",\n" +
    "                                \"price\": \"\",\n" +
    "                                \"valid_from\": \"\",\n" +
    "                                \"valid_through\": \"\",\n" +
    "                                \"vat_included\": false\n" +
    "                            }\n" +
    "                        }\n" +
    "                    },\n" +
    "                    \"states\": [\n" +
    "                        {\n" +
    "                            \"name\": \"Activated\",\n" +
    "                            \"value\": {\n" +
    "                                \"type\": \"boolean\",\n" +
    "                                \"value\": \"false\"\n" +
    "                            },\n" +
    "                            \"on_state_change\": {\n" +
    "                                \"reactor\": \"\"\n" +
    "                            }\n" +
    "                        }\n" +
    "                    ],\n" +
    "                    \"resources\": [\n" +
    "                        {\n" +
    "                            \"name\": \"ActivatedImage\",\n" +
    "                            \"resourceType\": \"ResourceType::Image::PNG\",\n" +
    "                            \"value\": {\n" +
    "                                \"resourceValueType\": \"ResourceValueType::URI\",\n" +
    "                                \"value\": \"https://cdndev.blockv.net/blockv/publisher/vatomic.prototyping/potato-head/eye.png\"\n" +
    "                            }\n" +
    "                        }\n" +
    "                    ],\n" +
    "                    \"visibility\": {\n" +
    "                        \"type\": \"container\",\n" +
    "                        \"value\": \"*\"\n" +
    "                    },\n" +
    "                    \"num_direct_clones\": 0,\n" +
    "                    \"geo_pos\": {\n" +
    "                        \"\$reql_type\$\": \"GEOMETRY\",\n" +
    "                        \"coordinates\": [\n" +
    "                            0,\n" +
    "                            0\n" +
    "                        ],\n" +
    "                        \"type\": \"Point\"\n" +
    "                    },\n" +
    "                    \"dropped\": false,\n" +
    "                    \"age\": 0\n" +
    "                },\n" +
    "                \"private\": {},\n" +
    "                \"version\": \"v1::vAtomType\",\n" +
    "                \"sync\": 1,\n" +
    "                 \"child_policy\": [\n" +
    "                        {\n" +
    "                            \"template_variation\": \"vatomic.prototyping::potatohead-part::v1::eyes::v1\",\n" +
    "                            \"creation_policy\": {\n" +
    "                                \"auto_create\": \"create_new\",\n" +
    "                                \"auto_create_count\": 1,\n" +
    "                                \"auto_create_count_random\": false,\n" +
    "                                \"weighted_choices\": [],\n" +
    "                                \"policy_count_min\": 0,\n" +
    "                                \"policy_count_max\": 1,\n" +
    "                                \"enforce_policy_count_min\": false,\n" +
    "                                \"enforce_policy_count_max\": true\n" +
    "                            },\n" +
    "                            \"count\": 0\n" +
    "                        },\n" +
    "                        {\n" +
    "                            \"template_variation\": \"vatomic.prototyping::potatohead-part::v1::ears::v1\",\n" +
    "                            \"creation_policy\": {\n" +
    "                                \"auto_create\": \"create_new\",\n" +
    "                                \"auto_create_count\": 1,\n" +
    "                                \"auto_create_count_random\": false,\n" +
    "                                \"weighted_choices\": [],\n" +
    "                                \"policy_count_min\": 0,\n" +
    "                                \"policy_count_max\": 1,\n" +
    "                                \"enforce_policy_count_min\": false,\n" +
    "                                \"enforce_policy_count_max\": true\n" +
    "                            },\n" +
    "                            \"count\": 0\n" +
    "                        },\n" +
    "                        {\n" +
    "                            \"template_variation\": \"vatomic.prototyping::potatohead-part::v1::hat::v1\",\n" +
    "                            \"creation_policy\": {\n" +
    "                                \"auto_create\": \"create_new\",\n" +
    "                                \"auto_create_count\": 1,\n" +
    "                                \"auto_create_count_random\": false,\n" +
    "                                \"weighted_choices\": [],\n" +
    "                                \"policy_count_min\": 0,\n" +
    "                                \"policy_count_max\": 1,\n" +
    "                                \"enforce_policy_count_min\": false,\n" +
    "                                \"enforce_policy_count_max\": true\n" +
    "                            },\n" +
    "                            \"count\": 0\n" +
    "                        },\n" +
    "                        {\n" +
    "                            \"template_variation\": \"vatomic.prototyping::potatohead-part::v1::nose::v1\",\n" +
    "                            \"creation_policy\": {\n" +
    "                                \"auto_create\": \"create_new\",\n" +
    "                                \"auto_create_count\": 1,\n" +
    "                                \"auto_create_count_random\": false,\n" +
    "                                \"weighted_choices\": [],\n" +
    "                                \"policy_count_min\": 0,\n" +
    "                                \"policy_count_max\": 1,\n" +
    "                                \"enforce_policy_count_min\": false,\n" +
    "                                \"enforce_policy_count_max\": true\n" +
    "                            },\n" +
    "                            \"count\": 0\n" +
    "                        },\n" +
    "                        {\n" +
    "                            \"template_variation\": \"vatomic.prototyping::potatohead-part::v1::mustache::v1\",\n" +
    "                            \"creation_policy\": {\n" +
    "                                \"auto_create\": \"create_new\",\n" +
    "                                \"auto_create_count\": 1,\n" +
    "                                \"auto_create_count_random\": false,\n" +
    "                                \"weighted_choices\": [],\n" +
    "                                \"policy_count_min\": 0,\n" +
    "                                \"policy_count_max\": 1,\n" +
    "                                \"enforce_policy_count_min\": false,\n" +
    "                                \"enforce_policy_count_max\": true\n" +
    "                            },\n" +
    "                            \"count\": 0\n" +
    "                        },\n" +
    "                        {\n" +
    "                            \"template_variation\": \"vatomic.prototyping::potatohead-part::v1::feet::v1\",\n" +
    "                            \"creation_policy\": {\n" +
    "                                \"auto_create\": \"create_new\",\n" +
    "                                \"auto_create_count\": 1,\n" +
    "                                \"auto_create_count_random\": false,\n" +
    "                                \"weighted_choices\": [],\n" +
    "                                \"policy_count_min\": 0,\n" +
    "                                \"policy_count_max\": 1,\n" +
    "                                \"enforce_policy_count_min\": false,\n" +
    "                                \"enforce_policy_count_max\": true\n" +
    "                            },\n" +
    "                            \"count\": 0\n" +
    "                        }\n" +
    "                    ]"+
    "            }"

  val action = "{\n" +
    "                \"name\": \"vatomic.prototyping::potatohead-part::v1::Action::Pickup\",\n" +
    "                \"meta\": {\n" +
    "                    \"created_by\": \"BLOCKv Backend\",\n" +
    "                    \"when_created\": \"2018-10-15T12:21:37Z\",\n" +
    "                    \"modified_by\": \"\",\n" +
    "                    \"when_modified\": \"2018-10-15T12:21:37Z\",\n" +
    "                    \"data_type\": \"v1::ActionType\"\n" +
    "                },\n" +
    "                \"properties\": {\n" +
    "                    \"name\": \"vatomic.prototyping::potatohead-part::v1::Action::Pickup\",\n" +
    "                    \"reactor\": \"blockv://v1/Pickup\",\n" +
    "                    \"wait\": true,\n" +
    "                    \"rollback\": false,\n" +
    "                    \"abort_on_pre_error\": true,\n" +
    "                    \"abort_on_post_error\": false,\n" +
    "                    \"abort_on_main_error\": true,\n" +
    "                    \"timeout\": 10000,\n" +
    "                    \"guest_user\": true,\n" +
    "                    \"state_impact\": [],\n" +
    "                    \"policy\": {\n" +
    "                        \"pre\": [],\n" +
    "                        \"rule\": \"\",\n" +
    "                        \"post\": []\n" +
    "                    },\n" +
    "                    \"params\": {\n" +
    "                        \"input\": [\n" +
    "                            \"this.id\"\n" +
    "                        ],\n" +
    "                        \"output\": [\n" +
    "                            \"v1::Error\"\n" +
    "                        ]\n" +
    "                    },\n" +
    "                    \"config\": {},\n" +
    "                    \"limit_per_user\": 0,\n" +
    "                    \"action_notification\": {\n" +
    "                        \"on\": false,\n" +
    "                        \"msg\": \"\",\n" +
    "                        \"custom\": {}\n" +
    "                    }\n" +
    "                }\n" +
    "            }"

  val face = "  {\n" +
    "                \"id\": \"2ef659a2-7c7c-4e05-ba83-92c84a90bba7\",\n" +
    "                \"template\": \"vatomic.prototyping::potatohead-part::v1\",\n" +
    "                \"meta\": {\n" +
    "                    \"created_by\": \"BLOCKv Backend\",\n" +
    "                    \"when_created\": \"2018-10-15T12:22:03Z\",\n" +
    "                    \"modified_by\": \"\",\n" +
    "                    \"when_modified\": \"2018-10-15T12:22:03Z\",\n" +
    "                    \"data_type\": \"v1::FaceType\"\n" +
    "                },\n" +
    "                \"properties\": {\n" +
    "                    \"display_url\": \"native://image\",\n" +
    "                    \"package_url\": \"nativeL//image\",\n" +
    "                    \"constraints\": {\n" +
    "                        \"bluetooth_le\": false,\n" +
    "                        \"contact_list\": false,\n" +
    "                        \"gps\": false,\n" +
    "                        \"three_d\": false,\n" +
    "                        \"view_mode\": \"icon\",\n" +
    "                        \"platform\": \"generic\",\n" +
    "                        \"quality\": \"high\"\n" +
    "                    },\n" +
    "                    \"resources\": [\n" +
    "                        \"ActivatedImage\"\n" +
    "                    ]\n" +
    "                }\n" +
    "            }"

  class Serialize {
    @JsonModule.Serialize
    val id = "test-id"
    @JsonModule.Serialize
    val count = 100
    @JsonModule.Serialize
    val decimal = 0.1000
    @JsonModule.Serialize(name = "template_id")
    val templateId = "example-template-id"
    @JsonModule.Serialize(path = "depth.test")
    val example = " depth test"
    @JsonModule.Serialize
    val list: List<String>

    init {
      val data = ArrayList<String>()
      data.add("hello")
      data.add("world")
      data.add("this")
      data.add("is")
      data.add("a")
      data.add("test")
      list = data
    }
  }

  @Test
  fun basicTest() {

    val temp = JsonModule()

    val data = JSONObject(vatom)

    try {

      val vatom = temp.deserialize<Vatom>(data, Vatom::class)
      System.out.println(temp.serialize(vatom!!)?.toString())
    } catch (e: Exception) {
      System.err.println(e)
    }
    Assert.assertTrue(true)
  }
}