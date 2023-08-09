package com.shop.model

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import org.ktorm.schema.*

interface UserInfo: Entity<UserInfo> {
    val id: Int
    val country: Int
    val phoneNumber: Long
    val userName: String?
    val businessName: String?
}

object UserInfos: Table<UserInfo>("user_info") {
    val id = int("id").primaryKey().bindTo { it.id }
    val country = int("country").bindTo { it.country }
    val phoneNumber = long("phone_number").bindTo { it.phoneNumber }
    val userName = text("user_name").bindTo { it.userName }
    val businessName = text("business_name").bindTo { it.businessName }

    @Serializable
    data class UserInfo(
        val id: Int?,
        val country: Int?,
        val phoneNumber: Long?,
        val userName: String?,
        val businessName: String?
    )
}