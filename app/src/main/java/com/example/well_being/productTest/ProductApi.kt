package com.example.well_being.productTest

import retrofit2.http.GET

interface ProductApi {
    @GET("addresses/products/1")
    suspend fun getProductById(): Product
}