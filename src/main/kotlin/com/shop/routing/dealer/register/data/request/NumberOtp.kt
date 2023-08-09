package com.shop.routing.dealer.register.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NumberOtp(
    @SerialName("country")
    val country: Int,

    @SerialName("phone_number")
    val phoneNumber: Long,

    @SerialName("otp")
    val otp: Long,

    @SerialName("type")
    val type: String
    )