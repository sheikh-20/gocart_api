package com.shop.data

import com.shop.dao.dealer.register.RegisterDao
import com.shop.model.UserInfo
import com.shop.model.UserInfos
import com.shop.routing.dealer.register.data.request.PhoneNumber
import kotlinx.coroutines.flow.Flow

interface UserInfoRepository {
    suspend fun readUserInfo(country: Int, phoneNumber: Long): UserInfos.UserInfo?
    suspend fun insertUserInfo(country: Int, phoneNumber: Long)
    suspend fun updateBusinessInfo(phoneNumber: Long, userName: String, businessName: String)
}

class DefaultUserInfoRepository(private val dao: RegisterDao): UserInfoRepository {

    override suspend fun readUserInfo(country: Int, phoneNumber: Long): UserInfos.UserInfo? {
        return dao.readUserInfo(country, phoneNumber)
    }

    override suspend fun insertUserInfo(country: Int, phoneNumber: Long) {
        dao.insertUserInfo(country, phoneNumber)
    }

    override suspend fun updateBusinessInfo(phoneNumber: Long, userName: String, businessName: String) {
        dao.updateBusinessInfo(phoneNumber, userName, businessName)
    }
}

