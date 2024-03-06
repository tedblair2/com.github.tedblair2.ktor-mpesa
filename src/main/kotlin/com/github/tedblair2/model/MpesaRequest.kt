package com.github.tedblair2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MpesaRequest(
    @SerialName("AccountReference")
    val accountReference: String?,
    @SerialName("Amount")
    val amount: String?,
    @SerialName("BusinessShortCode")
    val businessShortCode: String?,
    @SerialName("CallBackURL")
    val callBackURL: String?,
    @SerialName("PartyA")
    val partyA: String?,
    @SerialName("PartyB")
    val partyB: String?,
    @SerialName("Password")
    val password: String?,
    @SerialName("PhoneNumber")
    val phoneNumber: String?,
    @SerialName("Timestamp")
    val timestamp: String?,
    @SerialName("TransactionDesc")
    val transactionDesc: String?,
    @SerialName("TransactionType")
    val transactionType: String?
)