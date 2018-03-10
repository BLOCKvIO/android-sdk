package io.blockv.core.internal.net.rest.request

/**
 * Created by LordCheddar on 2018/03/10.
 */
class UploadAvatarRequest(val fieldName: String, val fileName: String, val type: String, val payload: ByteArray)