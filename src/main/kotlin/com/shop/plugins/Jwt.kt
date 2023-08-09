package com.shop.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.http.impl.auth.BasicScheme.authenticate

fun Application.configureJwt() {
//    val secret = environment.config.property("jwt.secret").getString()
//    val issuer = environment.config.property("jwt.issuer").getString()
//    val audience = environment.config.property("jwt.audience").getString()
//    val myRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "Access to 'hello'"
            verifier(
                JWT
                .require(Algorithm.HMAC256("secret"))
                .withAudience("127.0.0.1:8080/api/v1/dealer/verifyOtp")
                .withIssuer("127.0.0.1:8080")
                .build())
            validate { credential ->
                if (credential.payload.getClaim("number").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}