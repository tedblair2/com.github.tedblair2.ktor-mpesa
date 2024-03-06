package com.github.tedblair2.payment

import com.github.tedblair2.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import org.ehcache.config.units.MemoryUnit
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MpesaServiceImpl(private val httpClient: HttpClient) : MpesaService {

    private val storagePath=File("build/ehcache")
    private val persistenceConfig= CacheManagerPersistenceConfiguration(storagePath)
    private val resourcePool= ResourcePoolsBuilder.newResourcePoolsBuilder()
        .heap(100, EntryUnit.ENTRIES)
        .offheap(10,MemoryUnit.MB)
        .disk(50,MemoryUnit.MB,true)
    private val tokenCacheConfigurationBuilder= CacheConfigurationBuilder.newCacheConfigurationBuilder(
        String::class.javaObjectType,
        AccessToken::class.java,
        resourcePool
    ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(3550)))
    private val checkoutIdConfigBuilder=CacheConfigurationBuilder.newCacheConfigurationBuilder(
        String::class.javaObjectType,
        String::class.java,
        resourcePool
    ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(15)))

    private val cacheManager= CacheManagerBuilder.newCacheManagerBuilder()
        .with(persistenceConfig)
        .withCache("accessTokenCache",tokenCacheConfigurationBuilder)
        .withCache("checkOutIdCache",checkoutIdConfigBuilder)
        .build(true)

    private val tokenCache=cacheManager.getCache("accessTokenCache",String::class.javaObjectType,AccessToken::class.java)

    private val checkOutIdCache=cacheManager.getCache("checkOutIdCache",String::class.javaObjectType,String::class.java)

    override suspend fun initiateLipaNaMpesa():ApiResult<MpesaResponse>{
        val passkey=System.getenv("passkey")
        val businessCode=""
        val timestamp=generateTimeStamp()
        val password=generateBase64Value("$businessCode$passkey$timestamp")
        val phoneNumber=""
        val mpesaRequest=MpesaRequest(
            businessShortCode = businessCode,
            password = password,
            timestamp = timestamp,
            transactionType = "CustomerPayBillOnline",
            amount = "1",
            partyA = phoneNumber,
            partyB = businessCode,
            phoneNumber = phoneNumber,
            callBackURL = "https://2c8f-105-160-50-85.ngrok-free.app/callback",
            accountReference = "TED Travelers Co.",
            transactionDesc = "StkPush Test"
        )
        val accessTokenResult=getAccessToken()
        return try {
            when(accessTokenResult){
                is ApiResult.Success->{
                    ApiResult.Success(
                        httpClient.post("/mpesa/stkpush/v1/processrequest") {
                            header(HttpHeaders.Authorization,"Bearer ${accessTokenResult.data?.accessToken}")
                            contentType(ContentType.Application.Json)
                            setBody(mpesaRequest)
                        }.body<MpesaResponse>().also {
                            checkOutIdCache.put(phoneNumber,it.checkoutRequestID)
                        }
                    )
                }
                is ApiResult.Error->ApiResult.Error(accessTokenResult.error ?: "Error generating access token")
            }
        }catch (e:Exception){
            ApiResult.Error(e.message ?: "Error performing payment")
        }
    }

    override suspend fun queryMpesaPayment(): ApiResult<QueryResponse> {
        val passkey=System.getenv("passkey")
        val businessCode=""
        val timestamp=generateTimeStamp()
        val password=generateBase64Value("$businessCode$passkey$timestamp")
        val phoneNumber=""
        val checkOutId=checkOutIdCache[phoneNumber]

        checkOutId?.let {id->
            val result=getAccessToken()
            val queryRequest=QueryRequest(
                businessShortCode = businessCode,
                checkoutRequestID = id,
                password = password,
                timestamp = timestamp
            )
            return try {
                when(result){
                    is ApiResult.Success->{
                        ApiResult.Success(
                            httpClient.post("/mpesa/stkpushquery/v1/query"){
                                header(HttpHeaders.Authorization,"Bearer ${result.data?.accessToken}")
                                contentType(ContentType.Application.Json)
                                setBody(queryRequest)
                            }.body()
                        )
                    }
                    else->ApiResult.Error(result.error ?: "Error generating access token")
                }
            }catch (e:Exception){
                ApiResult.Error(e.message ?: "Error performing query")
            }
        } ?: return ApiResult.Error("No previous transaction for this number")
    }

    private suspend fun generateAccessToken():ApiResult<AccessToken>{
        val username=System.getenv("username")
        val passwd=System.getenv("password")
        val usernamePasswd="$username:$passwd"
        val authString=generateBase64Value(usernamePasswd)
        return try {
            ApiResult.Success(
                httpClient.get("/oauth/v1/generate"){
                    parameter("grant_type","client_credentials")
                    header(HttpHeaders.Authorization,"Basic $authString")
                }.body()
            )
        }catch (e:Exception){
            ApiResult.Error(e.message)
        }
    }

    private suspend fun getAccessToken():ApiResult<AccessToken>{
        val accessToken=tokenCache["token"]
        accessToken?.let {
            return ApiResult.Success(it)
        } ?: return generateAccessToken().also {
            if (it is ApiResult.Success){
                tokenCache.put("token",it.data)
            }
        }
    }

    private fun generateTimeStamp():String{
        val formatter= DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val currentDateTime= LocalDateTime.now()
        return currentDateTime.format(formatter)
    }

    private fun generateBase64Value(input:String):String{
        return Base64.getEncoder().encodeToString(input.toByteArray())
    }
}