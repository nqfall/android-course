package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.data.viewmodel.WeatherViewModel
import com.example.weatherapp.ui.CityListScreen
import com.example.weatherapp.ui.WeatherDetailScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme

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
