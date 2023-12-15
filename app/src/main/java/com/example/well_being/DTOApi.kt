package com.example.well_being

import com.example.well_being.productTest.Product
import retrofit2.http.GET

interface DTOApi {
    @GET("q3")
    suspend fun getDTO(): DTO
}