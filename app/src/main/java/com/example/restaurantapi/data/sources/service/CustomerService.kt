package com.example.restaurantapi.data.sources.service

import com.example.restaurantapi.data.model.CustomerResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path


interface CustomerService {

    @GET("customer")
  suspend  fun getCharacters(): Response<List<CustomerResponse>>




    @DELETE("customer/{id}")
    suspend fun deleteCharacter(@Path("id") id: Int): Response<ResponseBody>
}