package com.github.tedblair2.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StkCallback(
    @SerialName("CallbackMetadata")
    val callbackMetadata: CallbackMetadata?=null,
    @SerialName("CheckoutRequestID")
    val checkoutRequestID: String,
    @SerialName("MerchantRequestID")
    val merchantRequestID: String,
    @SerialName("ResultCode")
    val resultCode: Int,
    @SerialName("ResultDesc")
    val resultDesc: String
)