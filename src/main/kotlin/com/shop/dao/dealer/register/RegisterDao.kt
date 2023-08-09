package com.shop.dao.dealer.register

import com.shop.dao.DatabaseFactory
import com.shop.model.UserInfos
import com.shop.routing.dealer.register.data.request.BusinessInfo
import com.shop.routing.dealer.register.data.request.PhoneNumber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.ktorm.dsl.*
import javax.xml.crypto.Data

interface RegisterDao {
    suspend fun readUserInfo(country: Int, phoneNumber: Long): UserInfos.UserInfo?
    suspend fun insertUserInfo(country: Int, phoneNumber: Long)
    suspend fun updateBusinessInfo(phoneNumber: Long, userName: String, businessName: String)
}

object DefaultRegisterDao: RegisterDao {

    override suspend fun readUserInfo(country: Int, phoneNumber: Long): UserInfos.UserInfo? {
        val data = DatabaseFactory.database.from(UserInfos).select()
            .where { UserInfos.country eq country  }
            .where { UserInfos.phoneNumber eq phoneNumber }

        val result = data.map {
            UserInfos.UserInfo(id = it[UserInfos.id], country = it[UserInfos.country], phoneNumber = it[UserInfos.phoneNumber], userName = it[UserInfos.userName], businessName = it[UserInfos.businessName])
        }.lastOrNull()

        return result
    }

    override suspend fun insertUserInfo(country: Int, phoneNumber: Long) {
        val data = DatabaseFactory.database.insert(UserInfos) {
            set(it.country, country)
            set(it.phoneNumber, phoneNumber)
        }
    }

    override suspend fun updateBusinessInfo(phoneNumber: Long, userName: String, businessName: String) {
        DatabaseFactory.database.update(UserInfos) {
            set(it.userName, userName)
            set(it.businessName, businessName)
            where { it.phoneNumber eq phoneNumber }
        }
    }
}