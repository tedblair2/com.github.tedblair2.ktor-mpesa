package com.github.tedblair2.model

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
data class Item(
    @SerialName("Name")
    val name: String,
    @SerialName("Value")
    @Serializable(with = ValueSerializer::class)
    val value: Any?=null
)

@OptIn(ExperimentalSerializationApi::class)
class ValueSerializer : KSerializer<Any?>{
    override val descriptor: SerialDescriptor
        get() = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): Any? {
        val element=decoder.decodeSerializableValue(JsonElement.serializer())
        return when(element){
            is JsonPrimitive->{
                if (element.jsonPrimitive.isString){
                    element.jsonPrimitive.content
                }else if (element.jsonPrimitive.longOrNull != null){
                    element.jsonPrimitive.long
                }else if (element.jsonPrimitive.doubleOrNull != null){
                    element.jsonPrimitive.double
                }else{
                    null
                }
            }
            else->throw SerializationException("Unsupported type $element")
        }
    }

    override fun serialize(encoder: Encoder, value: Any?) {
        when(value){
            is String?->encoder.encodeNullableSerializableValue(String.serializer(),value)
            is Long?->encoder.encodeNullableSerializableValue(Long.serializer(),value)
            is Double?->encoder.encodeNullableSerializableValue(Double.serializer(),value)
            else->throw SerializationException("Unsupported type")
        }
    }
}