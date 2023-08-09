package com.shop.routing.dealer.register.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusinessInfo(
    @SerialName("user_name")
    val userName: String,

    @SerialName("business_name")
    val businessName: String
)
