package com.shop.routing.dealer.register.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ServerResponse(
    val code: Int,
    val message: String,
    val status: Boolean
)
