package com.example.restaurantapi.domain.modelo

import com.example.restaurantapi.data.model.OrderResponse
import java.time.LocalDate

data class Order (
 val id: Int,
 val customerId: Int,
 val orderDate: LocalDate,
 val tableId: Int,
 @Transient  var isSelected: Boolean = false
)

fun Order.toOrderResponse() : OrderResponse = OrderResponse(id, customerId, orderDate.toString(), tableId )

