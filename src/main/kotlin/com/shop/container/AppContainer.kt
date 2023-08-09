package com.shop.container

import com.shop.dao.dealer.register.DefaultRegisterDao
import com.shop.data.DefaultUserInfoRepository
import com.shop.data.UserInfoRepository

interface AppContainer {
    val repository: UserInfoRepository
}

class DefaultAppContainer: AppContainer {
    override val repository: UserInfoRepository by lazy {
        DefaultUserInfoRepository(DefaultRegisterDao)
    }
}