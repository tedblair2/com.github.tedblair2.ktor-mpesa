package com.github.tedblair2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryRequest(
    @SerialName("BusinessShortCode")
    val businessShortCode: String,
    @SerialName("CheckoutRequestID")
    val checkoutRequestID: String,
    @SerialName("Password")
    val password: String,
    @SerialName("Timestamp")
    val timestamp: String
)