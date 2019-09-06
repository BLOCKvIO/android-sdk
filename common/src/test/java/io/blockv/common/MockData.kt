package io.blockv.common

object MockData {

  val action = "{\"name\": \"io.mock::template::v1::Action::Transfer\"}"

  val face = "{\n" +
    "                \"id\": \"18d446f3-09c6-4720-851b-xxxx\",\n" +
    "                \"template\": \"io.mock::template::v1\",\n" +
    "                \"meta\": {\n" +
    "                    \"created_by\": \"BLOCKv Backend\",\n" +
    "                    \"when_created\": \"2018-09-04T14:10:39Z\",\n" +
    "                    \"when_modified\": \"2018-09-04T14:10:39Z\"\n" +
    "                },\n" +
    "                \"properties\": {\n" +
    "                    \"display_url\": \"https://cdn-public.vatomic.io/templates/block.mock/mock/v1/face/index.html\",\n" +
    "                    \"config\": {},\n" +
    "                    \"constraints\": {\n" +
    "                        \"view_mode\": \"fullscreen\",\n" +
    "                        \"platform\": \"android\"\n" +
    "                    },\n" +
    "                    \"resources\": []\n" +
    "                }\n" +
    "            }"

  val vatom = "{\n" +
    "                \"id\": \"0b1df9f8-784d-4dcf-8eae-xxxxx\",\n" +
    "                \"sync\": 0,\n" +
    "                \"when_created\": \"2018-10-30T15:55:10Z\",\n" +
    "                \"when_modified\": \"2018-10-30T15:55:37Z\",\n" +
    "                \"vAtom::vAtomType\": {\n" +
    "                    \"parent_id\": \".\",\n" +
    "                    \"publisher_fqdn\": \"vatomic.prototyping\",\n" +
    "                    \"root_type\": \"vAtom::vAtomType::DefinedFolderContainerType\",\n" +
    "                    \"owner\": \"f33161d2-5525-4aa5-ae7c-xxxxx\",\n" +
    "                    \"author\": \"2e1038f8-ffcd-4e91-aa81-xxxxx\",\n" +
    "                    \"template\": \"io.mock::template::v1\",\n" +
    "                    \"template_variation\": \"io.mock::template::v1::CombinedCard::PuzzleGame\",\n" +
    "                    \"notify_msg\": \"\",\n" +
    "                    \"title\": \"Build a puzzle\",\n" +
    "                    \"description\": \"Build this puzzle\",\n" +
    "                    \"category\": \"Kids\",\n" +
    "                    \"tags\": [\"mock\",\"data\"],\n" +
    "                    \"transferable\": true,\n" +
    "                    \"acquirable\": false,\n" +
    "                    \"tradeable\": true,\n" +
    "                    \"transferred_by\": \"2e1038f8-ffcd-4e91-aa81-xxxxx\",\n" +
    "                    \"cloned_from\": \"mock-xxxx\",\n" +
    "                    \"cloning_score\": 0.52,\n" +
    "                    \"in_contract\": false,\n" +
    "                    \"redeemable\": false,\n" +
    "                    \"in_contract_with\": \"\",\n" +
    "                    \"num_direct_clones\": 0,\n" +
    "                    \"commerce\": {\n" +
    "                        \"pricing\": {\n" +
    "                            \"pricingType\": \"\",\n" +
    "                            \"value\": {\n" +
    "                                \"currency\": \"ZAR\",\n" +
    "                                \"price\": \"1000\",\n" +
    "                                \"valid_from\": \"2018-10-30T15:55:10Z\",\n" +
    "                                \"valid_through\": \"2018-10-30T15:55:37Z\",\n" +
    "                                \"vat_included\": true\n" +
    "                            }\n" +
    "                        }\n" +
    "                    },\n" +
    "                    \"resources\": [\n" +
    "                        {\n" +
    "                            \"name\": \"ActivatedImage\",\n" +
    "                            \"resourceType\": \"ResourceType::Image::PNG\",\n" +
    "                            \"value\": {\n" +
    "                                \"value\": \"https://cdn.blockv.io/blockv/publisher/io.mock/mock/image_face_activated.png\"\n" +
    "                            }\n" +
    "                        },\n" +
    "                        {\n" +
    "                            \"name\": \"CardImage\",\n" +
    "                            \"resourceType\": \"ResourceType::Image::PNG\",\n" +
    "                            \"value\": {\n" +
    "                                \"value\": \"https://cdn.blockv.io/blockv/publisher/io.mock/mock/puzzle_game_card.png\"\n" +
    "                            }\n" +
    "                        },\n" +
    "                        {\n" +
    "                            \"name\": \"layerImage\",\n" +
    "                            \"resourceType\": \"ResourceType::Image::PNG\",\n" +
    "                            \"value\": {\n" +
    "                                \"value\": \"https://cdn.blockv.io/blockv/publisher/io.mock/mock/puzzle_base.png\"\n" +
    "                            }\n" +
    "                        }\n" +
    "                    ],\n" +
    "                    \"visibility\": {\n" +
    "                        \"type\": \"owner\",\n" +
    "                        \"value\": \"*\"\n" +
    "                    },\n" +
    "                    \"geo_pos\": {\n" +
    "                        \"\$reql_type\$\": \"GEOMETRY\",\n" +
    "                        \"coordinates\": [\n" +
    "                            0,\n" +
    "                            0\n" +
    "                        ],\n" +
    "                        \"type\": \"Point\"\n" +
    "                    },\n" +
    "                    \"dropped\": false,\n" +
    "                    \"child_policy\": [\n" +
    "                        {\n" +
    "                            \"template_variation\": \"io.mock::template::v2::Image::Puzzle_1\",\n" +
    "                            \"creation_policy\": {\n" +
    "                                \"auto_create\": \"create_new\",\n" +
    "                                \"auto_create_count\": 1,\n" +
    "                                \"auto_create_count_random\": false,\n" +
    "                                \"policy_count_min\": 0,\n" +
    "                                \"policy_count_max\": 1,\n" +
    "                                \"enforce_policy_count_min\": false,\n" +
    "                                \"enforce_policy_count_max\": true\n" +
    "                            },\n" +
    "                            \"count\": 0\n" +
    "                        },\n" +
    "                        {\n" +
    "                            \"template_variation\": \"io.mock::template::v2::Image::Puzzle_2\",\n" +
    "                            \"creation_policy\": {\n" +
    "                                \"auto_create\": \"create_new\",\n" +
    "                                \"auto_create_count\": 1,\n" +
    "                                \"auto_create_count_random\": false,\n" +
    "                                \"policy_count_min\": 0,\n" +
    "                                \"policy_count_max\": 1,\n" +
    "                                \"enforce_policy_count_min\": false,\n" +
    "                                \"enforce_policy_count_max\": true\n" +
    "                            },\n" +
    "                            \"count\": 0\n" +
    "                        },\n" +
    "                        {\n" +
    "                            \"template_variation\": \"io.mock::template::v2::Image::Puzzle_3\",\n" +
    "                            \"creation_policy\": {\n" +
    "                                \"auto_create\": \"create_new\",\n" +
    "                                \"auto_create_count\": 1,\n" +
    "                                \"auto_create_count_random\": false,\n" +
    "                                \"policy_count_min\": 0,\n" +
    "                                \"policy_count_max\": 1,\n" +
    "                                \"enforce_policy_count_min\": false,\n" +
    "                                \"enforce_policy_count_max\": true\n" +
    "                            },\n" +
    "                            \"count\": 0\n" +
    "                        },\n" +
    "                        {\n" +
    "                            \"template_variation\": \"io.mock::template::v2::Image::Puzzle_4\",\n" +
    "                            \"creation_policy\": {\n" +
    "                                \"auto_create\": \"create_new\",\n" +
    "                                \"auto_create_count\": 1,\n" +
    "                                \"auto_create_count_random\": false,\n" +
    "                                \"policy_count_min\": 0,\n" +
    "                                \"policy_count_max\": 1,\n" +
    "                                \"enforce_policy_count_min\": false,\n" +
    "                                \"enforce_policy_count_max\": true\n" +
    "                            },\n" +
    "                            \"count\": 0\n" +
    "                        }\n" +
    "                    ]\n" +
    "                },\n" +
    "                \"private\": {\n" +
    "                    \"allows_user_rotation\": true,\n" +
    "                    \"allows_user_zoom\": true,\n" +
    "                    \"auto_rotate_x\": 0,\n" +
    "                    \"auto_rotate_y\": 0,\n" +
    "                    \"auto_rotate_z\": 0,\n" +
    "                    \"play_animation\": true,\n" +
    "                    \"resources\": [],\n" +
    "                    \"scene_resource_name\": \"\"\n" +
    "                },\n" +
    "                \"faces\": [],\n" +
    "                \"actions\": []\n" +
    "            }"

  val user = "{\n" +
    "            \"id\": \"f33161d2-5525-4aa5-ae7c-xxxxx\",\n" +
    "            \"meta\": {\n" +
    "                \"when_created\": \"2017-09-22T14:29:12Z\",\n" +
    "                \"when_modified\": \"2018-11-07T17:54:51Z\"\n" +
    "            },\n" +
    "            \"properties\": {\n" +
    "                \"first_name\": \"mock\",\n" +
    "                \"last_name\": \"last name\",\n" +
    "                \"name_public\": true,\n" +
    "                \"is_password_set\": true,\n" +
    "                \"avatar_uri\": \"https://cdn.blockv.io/vatomic/avatars/f33161d2-5525-xxxxxx\",\n" +
    "                \"avatar_public\": true,\n" +
    "                \"birthday\": \"\",\n" +
    "                \"guest_id\": \"\",\n" +
    "                \"nonpush_notification\": false,\n" +
    "                \"language\": \"en\"\n" +
    "            }\n" +
    "        }"

  val assetProvider = "{\n" +
    "                \"name\": \"blockv\",\n" +
    "                \"type\": \"Cloudfront\",\n" +
    "                \"uri\": \"https://cdn.blockv.io\",\n" +
    "                \"descriptor\": {\n" +
    "                    \"Policy\": \"eyJTdGF0ZW1lbnQiOlt7IlJlc2xxxxxx\",\n" +
    "                    \"Signature\": \"G0FcTfEvKjN6jSgRY756flATBUiS-xxxxx-xxxxx\",\n" +
    "                    \"Key-Pair-Id\": \"APKAIxxxxx\"\n" +
    "                }\n" +
    "            }"

  val jwt = "{\n" +
    "            \"token\": \"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.xxxxxxx\",\n" +
    "            \"token_type\": \"Bearer\"\n" +
    "        }"

  val token = "{\n" +
    "            \"id\": \"af263c2a-7c25-xxxxxx\",\n" +
    "            \"meta\": {\n" +
    "                \"when_created\": \"2018-02-14T10:48:20Z\",\n" +
    "                \"when_modified\": \"2018-10-26T07:59:33Z\"\n" +
    "            },\n" +
    "            \"properties\": {\n" +
    "                \"app_id\": \"d7eaf120-xxxxx\",\n" +
    "                \"confirmed\": false,\n" +
    "                \"is_default\": true,\n" +
    "                \"token\": \"+27xxxxxxxxx\",\n" +
    "                \"token_type\": \"phone_number\",\n" +
    "                \"user_id\": \"f33161d2-xxxxxxx\",\n" +
    "                \"verify_code_expires\": \"2018-02-15T10:48:20Z\"\n" +
    "            }\n" +
    "        }"

  val activityThreadList = "{\"cursor\": \"1.524215664647836e+18\",\n" +
    "        \"threads\": [\n" +
    "            {\n" +
    "                \"name\": \"f33161d2-5525-xxxx:a1c3a467-cff8-xxxx\",\n" +
    "                \"when_modified\": 1541490309506766336,\n" +
    "                \"last_message\": {\n" +
    "                    \"msg_id\": 15414903095,\n" +
    "                    \"user_id\": \"f33161d2-5525-xxxx\",\n" +
    "                    \"vatoms\": [\n" +
    "                        \"07ad8195-8b9e-xxxx\"\n" +
    "                    ],\n" +
    "                    \"templ_vars\": [\n" +
    "                        \"io.mock::image-policy::v1::container::v1\"\n" +
    "                    ],\n" +
    "                    \"msg\": \"You sent <b>mock name</b> a <b>Mock test</b> vAtom.\",\n" +
    "                    \"action_name\": \"Transfer\",\n" +
    "                    \"when_created\": \"2018-11-06T07:45:09Z\",\n" +
    "                    \"triggered_by\": \"a1c3a467-cff8-xxxx\",\n" +
    "                    \"generic\": [\n" +
    "                        {\n" +
    "                            \"name\": \"ActivatedImage\",\n" +
    "                            \"resourceType\": \"ResourceType::Image::PNG\",\n" +
    "                            \"value\": {\n" +
    "                                \"value\": \"https://cdn.blockv.io/blockv/publisher/io.mock/mock/res_child_0.png\"\n" +
    "                            }\n" +
    "                        }\n" +
    "                    ],\n" +
    "                    \"geo_pos\": [\n" +
    "                        18.4131,\n" +
    "                        -33.9185\n" +
    "                    ]\n" +
    "                },\n" +
    "                \"last_message_user_info\": {\n" +
    "                    \"name\": \"mock name\",\n" +
    "                    \"avatar_uri\": \"https://cdn.blockv.io/vatomic/avatars/defaultAvatar.png\"\n" +
    "                }\n" +
    "            },\n" +
    "            {\n" +
    "                \"name\": \"f33161d2-5525-4xxxx:2e1038f8-ffcd-xxxx\",\n" +
    "                \"when_modified\": 1541077471276948480,\n" +
    "                \"last_message\": {\n" +
    "                    \"msg_id\": 154107747127,\n" +
    "                    \"user_id\": \"f33161d2-5525-xxxxx\",\n" +
    "                    \"vatoms\": [\n" +
    "                        \"07ad8195-8b9e-xxxxxx\"\n" +
    "                    ],\n" +
    "                    \"templ_vars\": [\n" +
    "                        \"io.mock::mock::v1::container::v1\"\n" +
    "                    ],\n" +
    "                    \"msg\": \"<b>Mock Name</b> sent you a <b>Mock test</b> vAtom.\",\n" +
    "                    \"action_name\": \"Transfer\",\n" +
    "                    \"when_created\": \"2018-11-01T13:04:31Z\",\n" +
    "                    \"triggered_by\": \"2e1038f8-ffcd-xxxxx9\",\n" +
    "                    \"generic\": [\n" +
    "                        {\n" +
    "                            \"name\": \"ActivatedImage\",\n" +
    "                            \"resourceType\": \"ResourceType::Image::PNG\",\n" +
    "                            \"value\": {\n" +
    "                                \"value\": \"https://cdn.blockv.io/blockv/publisher/io.mock/mock/res_child_0.png\"\n" +
    "                            }\n" +
    "                        }\n" +
    "                    ],\n" +
    "                    \"geo_pos\": [\n" +
    "                        18.4131,\n" +
    "                        -33.9185\n" +
    "                    ]\n" +
    "                },\n" +
    "                \"last_message_user_info\": {\n" +
    "                    \"name\": \"Mock Name\",\n" +
    "                    \"avatar_uri\": \"https://cdn.blockv.io/blockv/avatars/2e1038f8-ffcd-xxxxx\"\n" +
    "                }\n" +
    "            }]\n" +
    "         }"

  val activityMessage = " {\n" +
    "                    \"msg_id\": 154107747127,\n" +
    "                    \"user_id\": \"f33161d2-5525-xxxx\",\n" +
    "                    \"vatoms\": [\n" +
    "                        \"07ad8195-8b9e-xxxx\"\n" +
    "                    ],\n" +
    "                    \"templ_vars\": [\n" +
    "                        \"io.mock::template::v1::container::v1\"\n" +
    "                    ],\n" +
    "                    \"msg\": \"<b>Mock name</b> sent you a <b>Mock test</b> vAtom.\",\n" +
    "                    \"action_name\": \"Transfer\",\n" +
    "                    \"when_created\": \"2018-11-01T13:04:31Z\",\n" +
    "                    \"triggered_by\": \"2e1038f8-ffcd-xxxx9\",\n" +
    "                    \"generic\": [\n" +
    "                        {\n" +
    "                            \"name\": \"ActivatedImage\",\n" +
    "                            \"resourceType\": \"ResourceType::Image::PNG\",\n" +
    "                            \"value\": {\n" +
    "                                \"value\": \"https://cdn.blockv.io/blockv/publisher/io.mock/mock/res_child_0.png\"\n" +
    "                            }\n" +
    "                        }\n" +
    "                    ],\n" +
    "                    \"geo_pos\": [\n" +
    "                        18.4131,\n" +
    "                        -33.9185\n" +
    "                    ]\n" +
    "                }"
}