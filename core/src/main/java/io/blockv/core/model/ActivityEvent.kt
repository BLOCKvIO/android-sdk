package io.blockv.core.model

class ActivityEvent(val eventId: Long,
                    val targetUserId: String,
                    val triggeredBy: String,
                    val vatomIds: List<String>,
                    val resources: List<Resource>,
                    val message: String,
                    val actionName: String,
                    val whenCreated: String)
