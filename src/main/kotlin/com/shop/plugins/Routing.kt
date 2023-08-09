package com.shop.plugins

import com.shop.routing.dealer.register.registrationRouting
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.apache.http.impl.auth.BasicScheme.authenticate

fun Application.configureRouting() {
    routing {
        registrationRouting()
    }
}
