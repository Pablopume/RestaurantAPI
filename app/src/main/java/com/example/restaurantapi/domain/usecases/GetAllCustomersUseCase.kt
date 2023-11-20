package com.example.restaurantapi.domain.usecases


import com.example.restaurantapi.data.CustomerRepository
import com.example.restaurantapi.domain.modelo.Customer
import com.example.restaurantapi.utils.NetworkResult
import javax.inject.Inject

class GetAllCustomersUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Customer>> {
        return customerRepository.getCustomers()
    }
}