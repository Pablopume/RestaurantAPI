package com.example.restaurantapi.data

import com.example.restaurantapi.data.model.toCustomer
import com.example.restaurantapi.domain.modelo.Customer
import com.example.restaurantapi.data.sources.service.CustomerService
import com.example.restaurantapi.utils.NetworkResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
@ActivityRetainedScoped
class CustomerRepository @Inject constructor(private val customerService: CustomerService) {


    suspend fun getCustomers(): NetworkResult<List<Customer>> {
        var l: List<Customer> = emptyList()
        customerService.getCharacters().body()?.let {
            l = it.map { customerResponse ->
                customerResponse.toCustomer()
            }
        }
        return NetworkResult.Success(l)
    }

    //metodo para borrar un customer
    suspend fun deleteCustomer(id: Int): NetworkResult<String> {
        return try {
            val response = customerService.deleteCharacter(id)
            if (response.isSuccessful) {
                // Utilizar el cuerpo de la respuesta directamente como un String
                val responseBodyString = response.body()?.string() ?: "Deleted successfully"
                NetworkResult.Success(responseBodyString)
            } else {
                // Intentar obtener el mensaje del cuerpo de error si existe
                val errorBodyString = response.errorBody()?.string() ?: "Unknown error"
                NetworkResult.Error(errorBodyString)
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An error occurred")
        }
    }




}