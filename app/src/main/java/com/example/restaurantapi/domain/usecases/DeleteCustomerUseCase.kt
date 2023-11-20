package com.example.restaurantapi.domain.usecases


import com.example.restaurantapi.data.CustomerRepository
import com.example.restaurantapi.domain.modelo.Customer
import com.example.restaurantapi.utils.NetworkResult
import javax.inject.Inject

class DeleteCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
)   {
    suspend operator fun invoke(customer: Customer): NetworkResult<String> {
        return customerRepository.deleteCustomer(customer.id)
    }
}