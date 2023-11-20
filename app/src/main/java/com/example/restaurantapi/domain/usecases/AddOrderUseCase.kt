package com.example.restaurantapi.domain.usecases


import com.example.restaurantapi.data.OrderRepository
import com.example.restaurantapi.data.model.OrderResponse
import com.example.restaurantapi.domain.modelo.Order
import com.example.restaurantapi.utils.NetworkResult
import javax.inject.Inject

class AddOrderUseCase @Inject constructor(private val repository: OrderRepository) {
    suspend operator fun invoke(order: Order): NetworkResult<Order> {
        return repository.createOrder(order)
    }
}