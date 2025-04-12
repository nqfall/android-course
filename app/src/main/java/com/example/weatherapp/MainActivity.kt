package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme  {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val viewModel: WeatherViewModel = viewModel()
                    val selectedCity by viewModel.selectedCity.collectAsState()
                    if (selectedCity == null) {
                        CityListScreen(viewModel)
                    } else {
                        WeatherDetailScreen(viewModel)
                    }
                }
            }
        }
    }
}

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun CityListScreen(viewModel: WeatherViewModel) {
//    val cities by viewModel.cityList.collectAsState()
//    val coroutineScope = rememberCoroutineScope()
//
//    Column(modifier = Modifier
//        .fillMaxSize()
//        .padding(16.dp)) {
//
//        Text(
//            text = "Выберите город",
//            style = MaterialTheme.typography.headlineSmall,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        LazyColumn(
//            verticalArrangement = Arrangement.spacedBy(12.dp),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(cities) { city ->
//                Surface(
//                    shape = MaterialTheme.shapes.medium,
//                    tonalElevation = 2.dp,
//                    shadowElevation = 4.dp,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            coroutineScope.launch {
//                                viewModel.selectCity(city)
//                            }
//                        }
//                        .animateItemPlacement()
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp, vertical = 20.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = city.name,
//                            style = MaterialTheme.typography.bodyLarge,
//                            modifier = Modifier.weight(1f)
//                        )
//                        Icon(
//                            imageVector = Icons.Default.ArrowForward,
//                            contentDescription = "Перейти",
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun CityListScreen(viewModel: WeatherViewModel) {
    var cityInput by remember { mutableStateOf("") }
    val foundCity by viewModel.foundCity.collectAsState()
    val errorMessage by viewModel.cityError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Введите название города", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = cityInput,
            onValueChange = { cityInput = it },
            placeholder = { Text("Moscow") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.searchCity(cityInput) }) {
            Text("Поиск")
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            foundCity != null -> {
                Text(
                    text = "Город найден: ${foundCity!!.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { viewModel.selectCity(foundCity!!) }) {
                    Text("Показать погоду")
                }
            }

            errorMessage.isNotEmpty() -> {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}



//@Composable
//fun WeatherDetailScreen(viewModel: WeatherViewModel) {
//    val selectedDate by viewModel.selectedDate.collectAsState()
//    val weatherData by viewModel.weatherDataForDate.collectAsState()
//
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Button(onClick = { viewModel.previousDate() }) { Text("<") }
//            Text(text = selectedDate, fontSize = 24.sp, fontWeight = FontWeight.Bold)
//            Button(onClick = { viewModel.nextDate() }) { Text(">") }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Кнопка для возврата к списку городов
//        Button(onClick = { viewModel.clearSelectedCity() }, modifier = Modifier.padding(16.dp)) {
//            Text("Назад к списку городов")
//        }
//
//        if (weatherData.isEmpty()) {
//            Text("Нет данных о погоде", modifier = Modifier.align(Alignment.CenterHorizontally))
//        } else {
//            LazyColumn(modifier = Modifier.fillMaxSize()) {
//                items(weatherData) { item ->
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp)
//                            .background(Color(0xFFE0F7FA))
//                            .padding(16.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(text = item.time, fontWeight = FontWeight.SemiBold)
//                        Text(text = "🌧 ${item.precipitation} mm | 🌡 ${item.temperature}°C")
//                    }
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherDetailScreen(viewModel: WeatherViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val weatherData by viewModel.weatherDataForDate.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Навигация по датам
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousDate() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Day")
            }

            Text(
                text = selectedDate,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            IconButton(onClick = { viewModel.nextDate() }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Day")
            }
        }

        // Кнопка "Назад"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { viewModel.clearSelectedCity() }) {
                Text("Назад к списку городов")
            }
        }

        // Список погоды
        if (weatherData.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет данных о погоде")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(weatherData) { item ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 2.dp,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.time,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "🌧 ${item.precipitation} mm | 🌡 ${item.temperature}°C",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}


// --- Models ---
data class City(val name: String, val latitude: Double, val longitude: Double)
data class HourlyWeather(val time: String, val temperature: Double, val precipitation: Double)

// --- API Interfaces ---
//interface CityApiService {
//    @GET("v1/city")
//    suspend fun getCityCoordinates(
//        @Query("name") name: String,
//        @Query("country") country: String = "RU",
//        @Query("limit") limit: Int = 1,
//        @Query("X-Api-Key") apiKey: String = "bljB0zFb4kEGxOa+kZPlJw==56gnyIaLAzfPhJTS"
//    ): List<ApiCityResponse>
//}


//interface CityApiService {
//    @Headers("X-Api-Key: bljB0zFb4kEGxOa+kZPlJw==56gnyIaLAzfPhJTS")
//    @GET("v1/city")
//    suspend fun getCityCoordinates(
//        @Query("name") name: String,
//        @Query("country") country: String = "RU",
//        @Query("limit") limit: Int = 1,
//    ): List<ApiCityResponse>
//}

interface CityApiService {
    @Headers("X-Api-Key: bljB0zFb4kEGxOa+kZPlJw==56gnyIaLAzfPhJTS")
    @GET("v1/city")
    suspend fun getCityCoordinates(
        @Query("name") name: String
    ): List<ApiCityResponse>
}

data class ApiCityResponse(val name: String, val latitude: Double, val longitude: Double)

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

// --- ViewModel ---
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

    private val _cityList = MutableStateFlow(
        listOf(
            City("Москва", 55.75, 37.61),
            City("Санкт-Петербург", 59.94, 30.31),
            City("Новосибирск", 55.03, 82.92),
            City("Екатеринбург", 56.83, 60.60),
            City("Казань", 55.79, 49.12),
            City("Нижний Новгород", 56.33, 44.00),
            City("Челябинск", 55.15, 61.40),
            City("Самара", 53.20, 50.15),
            City("Омск", 54.99, 73.36),
            City("Ростов-на-Дону", 47.23, 39.72)
        )
    )
    val cityList: StateFlow<List<City>> = _cityList.asStateFlow()

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

//    fun searchCity(query: String) {
//        viewModelScope.launch {
//            try {
//                _cityError.value = ""
//                _foundCity.value = null
//
//                val response = cityApi.getCityCoordinates(name = query.trim())
//                if (response.isNotEmpty()) {
//                    val result = response[0]
//                    _foundCity.value = City(result.name, result.latitude, result.longitude)
//                } else {
//                    _cityError.value = "Город не найден"
//                }
//            } catch (e: Exception) {
//                _cityError.value = "Ошибка при поиске: ${e.localizedMessage}"
//            }
//        }
//    }

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
        private fun currentDate(): String = SimpleDateFormat("dd.MM", Locale.getDefault()).format(Date())
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
