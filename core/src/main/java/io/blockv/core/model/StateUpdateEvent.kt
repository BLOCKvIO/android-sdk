package io.blockv.core.model

import org.json.JSONObject

class StateUpdateEvent(val eventId:String,
                       val operation: String,
                       val vatomId: String,
                       val vatomProperties: JSONObject,
                       val whenModified: String)