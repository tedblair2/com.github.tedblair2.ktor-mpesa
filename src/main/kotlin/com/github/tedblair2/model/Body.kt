package com.github.tedblair2.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Body(
    @SerialName("stkCallback")
    val stkCallback: StkCallback
)