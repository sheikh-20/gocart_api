package com.shop.routing.dealer.register

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.shop.container.DefaultAppContainer
import com.shop.routing.dealer.register.data.request.BusinessInfo
import com.shop.routing.dealer.register.data.request.NumberOtp
import com.shop.routing.dealer.register.data.request.PhoneNumber
import com.shop.routing.dealer.register.data.response.SendToken
import com.shop.routing.dealer.register.data.response.SendUserInfo
import com.shop.routing.dealer.register.data.response.ServerResponse
import com.shop.routing.dealer.register.data.response.ServerResponseJwt
import com.twilio.exception.ApiException
import com.twilio.rest.verify.v2.service.Verification
import com.twilio.rest.verify.v2.service.VerificationCheck
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.http.impl.auth.BasicScheme.authenticate
import java.util.*

fun Route.registrationRouting() {

    val appContainer = DefaultAppContainer()
    val country = listOf<Int>(91)

    post("/api/v1/dealer/numberVerify") {
        val data = call.receive<PhoneNumber>()

        when {
            country.contains(data.country).not() -> {
                call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid country code", status = false),
                    status = HttpStatusCode.BadRequest)
            }
            data.phoneNumber.toString().length != 10 -> {
                call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid number", status = false),
                    status = HttpStatusCode.BadRequest)
            }
            else -> {
                if (appContainer.repository.readUserInfo(data.country, data.phoneNumber) != null) {
                    call.respond(ServerResponse(code = HttpStatusCode.OK.value, message = "number exists", status = true))
                } else {
                    call.respond(ServerResponse(code = HttpStatusCode.OK.value, message = "number does not exist", status = false))
                }
            }
        }
    }

    post("/api/v1/dealer/sendOtp") {
        val data = call.receive<PhoneNumber>()

        when {
            country.contains(data.country).not() -> {
                call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid country code", status = false),
                    status = HttpStatusCode.BadRequest)
            }
            data.phoneNumber.toString().length != 10 -> {
                call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid number", status = false),
                    status = HttpStatusCode.BadRequest)
            }
            else -> {
                try {
                    val verification = Verification.creator("VAc301cf9835ef607de15ce6bd5cc7594f", "+${data.country}${data.phoneNumber}", "sms")
                    verification.create()
                    call.respond(ServerResponse(code = HttpStatusCode.OK.value, message = "OTP sent successfully", status = true))
                } catch (exception: ApiException) {
                    call.respond(message = ServerResponse(code = HttpStatusCode.OK.value, message = "OTP sent failed!", status = false))
                }
            }
        }
    }

    post("/api/v1/dealer/verifyOtp") {
        val data = call.receive<NumberOtp>()

        when {
            country.contains(data.country).not() -> {
                call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid country code", status = false),
                    status = HttpStatusCode.BadRequest)
            }
            data.phoneNumber.toString().length != 10 -> {
                call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid number", status = false),
                    status = HttpStatusCode.BadRequest)
            }
            data.otp.toString().length != 6 -> {
                call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid otp", status = false),
                    status = HttpStatusCode.BadRequest)
            }
            data.type != "signup" && data.type != "login" -> {
                call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid type", status = false),
                    status = HttpStatusCode.BadRequest)
            }
            else -> {
                try {
                    val verification = VerificationCheck.creator("VAc301cf9835ef607de15ce6bd5cc7594f")
                        .setTo("+${data.country}${data.phoneNumber}")
                        .setCode(data.otp.toString())
                        .create()

                    if (verification.valid) {
                        if (data.type == "signup") {
                            appContainer.repository.insertUserInfo(country = data.country, phoneNumber = data.phoneNumber)

                            val token = JWT.create()
                                .withAudience("127.0.0.1:8080/api/v1/dealer/verifyOtp")
                                .withIssuer("127.0.0.1:8080")
                                .withClaim("number", data.phoneNumber)
                                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                                .sign(Algorithm.HMAC256("secret"))

                            call.respond(ServerResponseJwt<SendToken>(code = HttpStatusCode.OK.value, data = SendToken(token), message = "Account created successfully", status = true))
                        } else {
                            val token = JWT.create()
                                .withAudience("127.0.0.1:8080/api/v1/dealer/verifyOtp")
                                .withIssuer("127.0.0.1:8080")
                                .withClaim("number", data.phoneNumber)
//                                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                                .sign(Algorithm.HMAC256("secret"))

                            call.respond(ServerResponseJwt<SendToken>(code = HttpStatusCode.OK.value, data = SendToken(token), message = "Login successfully", status = true))
                        }

                    } else {
                        call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid otp", status = false),
                            status = HttpStatusCode.BadRequest)
                    }
                } catch (exception: ApiException) {
                    call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid otp", status = false),
                        status = HttpStatusCode.BadRequest)
                }
            }
        }
    }

    authenticate("auth-jwt") {
//        get("/api/v1/dealer/home") {
//            val principal = call.principal<JWTPrincipal>()
//            val username = principal!!.payload.getClaim("number").asString()
//            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
//            call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
//        }

        get("/api/v1/dealer/home") {
            val principal = call.principal<JWTPrincipal>()
            if (principal != null) {
                call.respond(ServerResponseJwt<SendUserInfo>(code = HttpStatusCode.OK.value, data = SendUserInfo(), message = "Data", status = true))
            } else {
                call.respond(message = ServerResponseJwt<SendUserInfo>(code = HttpStatusCode.BadRequest.value, data = null, message = "Invalid otp", status = false), status = HttpStatusCode.BadRequest)
            }
        }

        post("/api/v1/dealer/businessInfo") {
            val data = call.receive<BusinessInfo>()

            val principal = call.principal<JWTPrincipal>()
            if (principal != null) {
                when {
                    data.userName.isEmpty() -> {
                        call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid user name", status = false),
                            status = HttpStatusCode.BadRequest)
                    }
                    data.businessName.isEmpty() -> {
                        call.respond(message = ServerResponse(code = HttpStatusCode.BadRequest.value, message = "Invalid business name", status = false),
                            status = HttpStatusCode.BadRequest)
                    }
                    else -> {
                        appContainer.repository.updateBusinessInfo(principal.payload.getClaim("number").asLong(), data.userName, data.businessName)
                        call.respond(ServerResponseJwt<SendUserInfo>(code = HttpStatusCode.OK.value, data = null, message = "Business info updated", status = true))
                    }
                }

//                call.respond(ServerResponseJwt<SendUserInfo>(code = HttpStatusCode.OK.value, data = SendUserInfo(), message = "Data", status = true))
            } else {
                call.respond(message = ServerResponseJwt<SendUserInfo>(code = HttpStatusCode.BadRequest.value, data = null, message = "Invalid otp", status = false), status = HttpStatusCode.BadRequest)
            }
        }
    }
}