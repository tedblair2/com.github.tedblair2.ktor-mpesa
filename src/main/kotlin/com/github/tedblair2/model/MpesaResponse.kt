package com.github.tedblair2.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MpesaResponse(
    @SerialName("CheckoutRequestID")
    val checkoutRequestID: String?,
    @SerialName("CustomerMessage")
    val customerMessage: String?,
    @SerialName("MerchantRequestID")
    val merchantRequestID: String?,
    @SerialName("ResponseCode")
    val responseCode: String?,
    @SerialName("ResponseDescription")
    val responseDescription: String?
)