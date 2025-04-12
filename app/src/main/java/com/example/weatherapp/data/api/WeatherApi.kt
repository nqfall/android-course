package com.example.weatherapp.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,precipitation",
        @Query("forecast_days") forecastDays: Int = 16,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}

data class WeatherResponse(val hourly: HourlyData)
data class HourlyData(val time: List<String>, val temperature_2m: List<Double>, val precipitation: List<Double>)