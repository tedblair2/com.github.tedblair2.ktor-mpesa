package com.github.tedblair2.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CallbackMetadata(
    @SerialName("Item")
    val item: List<Item>
)