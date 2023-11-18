package com.example.restaurantapi.data.sources.service

import com.example.restaurantapi.data.model.CustomerResponse
import retrofit2.Response
import retrofit2.http.GET




interface CustomerService {

    @GET("customer")
  suspend  fun getCharacters(): Response<List<CustomerResponse>>
}