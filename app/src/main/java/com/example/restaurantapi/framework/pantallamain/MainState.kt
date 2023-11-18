package com.example.restaurantapi.framework.pantallamain

import com.example.restaurantapi.domain.modelo.Customer

data class MainState (val personas: List<Customer> = emptyList(),
                 val personasSeleccionadas: List<Customer> = emptyList(),
                 val selectMode: Boolean = false,
                 val error: String? = null,)