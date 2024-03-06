package com.github.tedblair2.di

import com.github.tedblair2.payment.MpesaService
import com.github.tedblair2.payment.MpesaServiceImpl
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule= module {
    single {
        HttpClient(){
            install(Logging){
                level=LogLevel.ALL
            }
            install(DefaultRequest){
                url("https://sandbox.safaricom.co.ke")
            }
            install(ContentNegotiation){
                json(Json {
                    ignoreUnknownKeys=true
                })
            }
        }
    }
    single<MpesaService> {
        MpesaServiceImpl(get())
    }
}