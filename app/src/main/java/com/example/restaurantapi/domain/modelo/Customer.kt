package com.example.restaurantapi.domain.modelo

import java.time.LocalDate

data class Customer(
    val id: Int,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val dob: LocalDate,
    var isSelected: Boolean = false,
)