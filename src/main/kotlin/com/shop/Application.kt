package com.shop

import com.shop.dao.DatabaseFactory
import com.shop.plugins.configureJwt
import com.shop.plugins.configureRouting
import com.shop.plugins.configureSerialization
import com.twilio.Twilio
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val ACCOUNT_SID = "ACcbb11c6edb96576845062039878c314e"
private const val AUTH_TOKEN = "871ddfb6e26a85aa9063e291d6137e24"


fun main() {
    runBlocking {
        launch {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN)
        }
    }

    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureJwt()
    configureRouting()
}
