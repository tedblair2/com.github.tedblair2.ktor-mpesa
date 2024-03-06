package com.github.tedblair2.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryResponse(
    @SerialName("CheckoutRequestID")
    val checkoutRequestID: String,
    @SerialName("MerchantRequestID")
    val merchantRequestID: String,
    @SerialName("ResponseCode")
    val responseCode: String,
    @SerialName("ResponseDescription")
    val responseDescription: String,
    @SerialName("ResultCode")
    val resultCode: String,
    @SerialName("ResultDesc")
    val resultDesc: String
)