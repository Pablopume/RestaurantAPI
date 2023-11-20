package com.example.restaurantapi.data

import android.annotation.SuppressLint
import com.example.restaurantapi.data.model.OrderResponse
import com.example.restaurantapi.data.model.toOrder
import com.example.restaurantapi.data.sources.service.OrderService
import com.example.restaurantapi.domain.modelo.Order
import com.example.restaurantapi.domain.modelo.toOrderResponse
import com.example.restaurantapi.utils.NetworkResult
import okhttp3.ResponseBody
import java.time.LocalDate
import javax.inject.Inject

class OrderRepository @Inject constructor(private val orderService: OrderService) {


    suspend fun getOrdersPorId(id: Int): List<Order> {
        var list: List<Order> = getOrders().data ?: emptyList()
        list = list.filter { it.customerId == id }
        return list
    }

    suspend fun getOrders(): NetworkResult<List<Order>> {
        var list: List<Order> = emptyList()
        orderService.getOrders().body()?.let {
            list = it.map { orderResponse ->
                orderResponse.toOrder()
            }
        }
        return NetworkResult.Success(list)
    }

    suspend fun createOrder(order: Order): NetworkResult<Order> {
        return try {
            val response = orderService.createOrder(order.toOrderResponse())
            if (response.isSuccessful) {
                // Suponiendo que el cuerpo de la respuesta es un JSON que se mapea a OrderResponse
                response.body()?.let { orderResponse ->
                    // Convierte OrderResponse a tu dominio modelo Order
                    NetworkResult.Success(orderResponse.toOrder())
                } ?: NetworkResult.Error("No order returned")
            } else {
                // Intentar obtener el mensaje del cuerpo de error si existe
                val errorBodyString = response.errorBody()?.string() ?: "Unknown error"
                NetworkResult.Error(errorBodyString)
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun deleteOrder(id: Int): NetworkResult<String> {
        val response = orderService.deleteOrder(id)
        if (response.isSuccessful) {
            // Utilizar el cuerpo de la respuesta directamente como un String
            val responseBodyString = response.body()?.string() ?: "Deleted successfully"
            return NetworkResult.Success(responseBodyString)
        } else {
            // Intentar obtener el mensaje del cuerpo de error si existe
            val errorBodyString = response.errorBody()?.string() ?: "Unknown error"
            return NetworkResult.Error(errorBodyString)
        }

    }
}