package com.github.tedblair2.plugins

import com.github.tedblair2.model.ApiResult
import com.github.tedblair2.model.PaymentInfo
import com.github.tedblair2.payment.MpesaService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.get as get1

fun Application.configureRouting(mpesaService: MpesaService=get1()) {
    routing {
        get {
            val result=mpesaService.initiateLipaNaMpesa()
            when(result){
                is ApiResult.Success->{
                    call.respond(HttpStatusCode.OK,result.data!!)
                }
                else->call.respond(HttpStatusCode.NotImplemented,"Error implementing payment")
            }
        }
        post("/callback") {
            val result=call.receive<PaymentInfo>()
            println(result)
            call.respond(HttpStatusCode.OK)
        }
        get("/query") {
            val result=mpesaService.queryMpesaPayment()
            when(result){
                is ApiResult.Success->{
                    if (result.data?.resultCode?.toIntOrNull()!! > 0){
                        call.respond(HttpStatusCode.NotImplemented, mapOf("result" to result.data.resultDesc))
                    }else{
                        call.respond(HttpStatusCode.OK, mapOf("result" to "Payment Processed Successfully"))
                    }
                }
                is ApiResult.Error->call.respond(HttpStatusCode.NotImplemented, mapOf("error" to result.error))
            }
        }
    }
}
