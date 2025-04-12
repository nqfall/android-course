package com.example.weatherapp.data.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CityApiService {
    @Headers("X-Api-Key: bljB0zFb4kEGxOa+kZPlJw==56gnyIaLAzfPhJTS")
    @GET("v1/city")
    suspend fun getCityCoordinates(
        @Query("name") name: String
    ): List<ApiCityResponse>
}

data class ApiCityResponse(val name: String, val latitude: Double, val longitude: Double)