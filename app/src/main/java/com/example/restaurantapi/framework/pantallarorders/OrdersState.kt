package com.example.restaurantapi.framework.pantallarorders

import com.example.restaurantapi.domain.modelo.Customer
import com.example.restaurantapi.domain.modelo.Order

data class OrdersState (val personas: List<Order> = emptyList(),
                        val personasSeleccionadas: List<Order> = emptyList(),
                        val selectMode: Boolean = false,
                        val error: String? = null,)