package com.example.restaurantapi.domain.usecases


import com.example.restaurantapi.data.OrderRepository
import com.example.restaurantapi.domain.modelo.Order
import com.example.restaurantapi.utils.NetworkResult
import javax.inject.Inject

class DeleteOrderUseCase @Inject constructor(private val repository: OrderRepository) {
    suspend operator fun invoke(order: Order): NetworkResult<String> {
        return repository.deleteOrder(order.id)
    }
}