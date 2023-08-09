package com.shop.dao

import org.ktorm.database.Database

object DatabaseFactory {
    private const val jdbcUrl = "jdbc:mysql://localhost:3306/gocart"
    private const val driver = "com.mysql.cj.jdbc.Driver"
    private const val userName = "root"
    private const val password = "Sheikh10/3/2002"

    lateinit var database: Database

    fun init() {
        database = Database.connect(
            url = jdbcUrl,
            driver = driver,
            user = userName,
            password = password
        )
    }
}