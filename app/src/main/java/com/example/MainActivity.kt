package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppNavDestination
import com.example.ui.components.BotanicalBottomBar
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlantDetailScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PlantViewModel
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {

    private val plantViewModel: PlantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory App Check configuration before UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                BotanicalApp(viewModel = plantViewModel)
            }
        }
    }
}

@Composable
fun BotanicalApp(viewModel: PlantViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsState()
    val selectedPlant by viewModel.selectedPlant.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (selectedPlant == null) {
                BotanicalBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { dest ->
                        viewModel.navigateTo(dest)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (selectedPlant == null) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            val plant = selectedPlant
            if (plant != null) {
                PlantDetailScreen(
                    plant = plant,
                    viewModel = viewModel,
                    onBack = { viewModel.clearSelectedPlant() }
                )
            } else {
                when (currentDestination) {
                    AppNavDestination.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToScan = { viewModel.navigateTo(AppNavDestination.SCANNER) },
                        onOpenPlantDetail = { chosen -> viewModel.selectPlant(chosen) }
                    )
                    AppNavDestination.SCANNER -> ScannerScreen(
                        viewModel = viewModel,
                        onClose = { viewModel.navigateTo(AppNavDestination.HOME) },
                        onViewPlantDetail = {
                            // After saving, selectedPlant is updated by ViewModel
                        }
                    )
                    AppNavDestination.CALENDAR -> CalendarScreen(
                        viewModel = viewModel,
                        onSelectPlant = { chosen -> viewModel.selectPlant(chosen) }
                    )
                    AppNavDestination.HISTORY -> HistoryScreen(
                        viewModel = viewModel,
                        onSelectPlant = { chosen -> viewModel.selectPlant(chosen) }
                    )
                    AppNavDestination.SETTINGS -> SettingsScreen()
                }
            }
        }
    }
}
