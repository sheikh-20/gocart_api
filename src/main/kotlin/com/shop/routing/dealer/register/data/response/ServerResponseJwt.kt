package com.shop.routing.dealer.register.data.response

import com.sun.org.apache.xpath.internal.operations.Bool
import kotlinx.serialization.Serializable

@Serializable
data class ServerResponseJwt<T>(
    val code: Int,
    val data: T?,
    val message: String,
    val status: Boolean
    )

@Serializable
data class SendToken(val token: String)

@Serializable
data class SendUserInfo(val name: String = "Welcome to gocart")


