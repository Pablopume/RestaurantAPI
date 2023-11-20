package com.example.restaurantapi.data.model

import com.example.restaurantapi.domain.modelo.Order
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class OrderResponse (
    @SerializedName("id")
    val id: Int,
    @SerializedName("customerId")
    val customerId: Int,
    @SerializedName("orderDate")
    val orderDate: String,
    @SerializedName("tableId")
    val tableId: Int,
)
fun OrderResponse.toOrder() : Order = Order(id, customerId, LocalDate.parse(orderDate), tableId )