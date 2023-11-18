package com.example.restaurantapi.data

import com.example.restaurantapi.data.model.toCustomer
import com.example.restaurantapi.domain.modelo.Customer
import com.example.restaurantapi.data.sources.service.CustomerService
import com.example.restaurantapi.utils.NetworkResultt
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
@ActivityRetainedScoped
class RepositoryCustomers @Inject constructor(private val customerService: CustomerService) {


    suspend fun getCustomers(): NetworkResultt<List<Customer>> {
        var l: List<Customer> = emptyList()
        customerService.getCharacters().body()?.let {
            l = it.map { customerResponse ->
                customerResponse.toCustomer()
            }
        }
        return NetworkResultt.Success(l)
    }

}