package com.example.weatherapp.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.api.CityApiService
import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.api.WeatherResponse
import com.example.weatherapp.data.model.City
import com.example.weatherapp.data.model.HourlyWeather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeatherViewModel : ViewModel() {
    private val cityApi = Retrofit.Builder()
        .baseUrl("https://api.api-ninjas.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CityApiService::class.java)

    private val weatherApi = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApiService::class.java)

    private val _selectedCity = MutableStateFlow<City?>(null)
    val selectedCity: StateFlow<City?> = _selectedCity.asStateFlow()

    private val _selectedDate = MutableStateFlow(currentDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _weatherDataForDate = MutableStateFlow<List<HourlyWeather>>(emptyList())
    val weatherDataForDate: StateFlow<List<HourlyWeather>> = _weatherDataForDate.asStateFlow()

    private var weatherCache: WeatherResponse? = null

    private val _foundCity = MutableStateFlow<City?>(null)
    val foundCity: StateFlow<City?> = _foundCity.asStateFlow()

    private val _cityError = MutableStateFlow("")
    val cityError: StateFlow<String> = _cityError.asStateFlow()

    fun searchCity(query: String) {
        viewModelScope.launch {
            try {
                _cityError.value = ""
                _foundCity.value = null

                val response = cityApi.getCityCoordinates(
                    name = query.trim(),
                )

                if (response.isNotEmpty()) {
                    val result = response[0]
                    _foundCity.value = City(result.name, result.latitude, result.longitude)
                } else {
                    _cityError.value = "Город не найден"
                }
            } catch (e: Exception) {
                _cityError.value = "Ошибка при поиске: ${e.localizedMessage}"
            }
        }
    }



    fun selectCity(city: City) {
        _selectedCity.value = city
        viewModelScope.launch {
            loadWeather(city)
            _selectedDate.value = currentDate() // <- обновить заново!
            updateWeatherForDate(currentDate())
        }
    }

    private suspend fun loadWeather(city: City) {
        val response = weatherApi.getForecast(city.latitude, city.longitude)
        Log.d("API", response.toString())
        weatherCache = response
        updateWeatherForDate(currentDate())
    }

    private fun updateWeatherForDate(date: String) {
        val apiPrefix = dateToApiFormat(date) // "2025-04-12"

        weatherCache?.let { data ->
            val filtered = data.hourly.time.mapIndexedNotNull { index, time ->
                if (time.startsWith(apiPrefix)) {
                    val hour = time.substringAfter("T") // "14:00"
                    HourlyWeather(
                        time = hour,
                        temperature = data.hourly.temperature_2m[index],
                        precipitation = data.hourly.precipitation[index]
                    )
                } else null
            }

            _weatherDataForDate.value = filtered
            Log.d("WeatherViewModel", "Loaded ${filtered.size} hourly items for $apiPrefix")

        } ?: run {
            _weatherDataForDate.value = emptyList()
        }
    }


    fun previousDate() {
        _selectedDate.value = adjustDate(_selectedDate.value, -1)
        updateWeatherForDate(_selectedDate.value)
    }

    fun nextDate() {
        _selectedDate.value = adjustDate(_selectedDate.value, 1)
        updateWeatherForDate(_selectedDate.value)
    }

    fun clearSelectedCity() {
        _selectedCity.value = null
    }

    companion object {
        private fun currentDate(): String = SimpleDateFormat("dd.MM", Locale.getDefault()).format(
            Date()
        )
        private fun adjustDate(dateStr: String, offset: Int): String {
            val format = SimpleDateFormat("dd.MM", Locale.getDefault())
            val date = format.parse(dateStr) ?: Date()
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DAY_OF_YEAR, offset)
            return format.format(calendar.time)
        }

        private fun dateToApiFormat(dateStr: String): String {
            val inputFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
            val parsed = inputFormat.parse(dateStr) ?: Date()

            val calendar = Calendar.getInstance()
            calendar.time = parsed

            // Устанавливаем текущий год
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            calendar.set(Calendar.YEAR, currentYear)

            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return outputFormat.format(calendar.time)
        }

    }
}