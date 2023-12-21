package com.example.well_being

import retrofit2.http.GET

interface DTOApi {
    @GET("q3")
    suspend fun getDTO(): UserHealthDto
}