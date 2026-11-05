package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.PlantIdentificationResult
import com.example.data.local.AppDatabase
import com.example.data.local.PlantEntity
import com.example.data.repository.PlantRepository
import com.example.ui.components.AppNavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "PlantViewModel"

class PlantViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = PlantRepository(database.plantDao())

    val allPlants: StateFlow<List<PlantEntity>> = repository.allPlants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeFilter = MutableStateFlow("All")
    val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

    val filteredPlants: StateFlow<List<PlantEntity>> = combine(allPlants, _searchQuery, _activeFilter) { plants, query, filter ->
        plants.filter { plant ->
            val matchesQuery = query.isBlank() ||
                    plant.name.contains(query, ignoreCase = true) ||
                    plant.scientificName.contains(query, ignoreCase = true) ||
                    plant.nickname.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "Herbs" -> plant.plantType.contains("Herb", ignoreCase = true) || plant.name.contains("Basil", true) || plant.name.contains("Rucola", true)
                "Vegetables" -> plant.plantType.contains("Vegetable", ignoreCase = true) || plant.name.contains("Lettuce", true) || plant.name.contains("Tomato", true)
                "Favorites" -> plant.isFavorite
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentDestination = MutableStateFlow(AppNavDestination.HOME)
    val currentDestination: StateFlow<AppNavDestination> = _currentDestination.asStateFlow()

    private val _selectedPlant = MutableStateFlow<PlantEntity?>(null)
    val selectedPlant: StateFlow<PlantEntity?> = _selectedPlant.asStateFlow()

    // Scanner State
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _scanResult = MutableStateFlow<PlantIdentificationResult?>(null)
    val scanResult: StateFlow<PlantIdentificationResult?> = _scanResult.asStateFlow()

    private val _scannedBitmap = MutableStateFlow<Bitmap?>(null)
    val scannedBitmap: StateFlow<Bitmap?> = _scannedBitmap.asStateFlow()

    private val _scannerErrorMessage = MutableStateFlow<String?>(null)
    val scannerErrorMessage: StateFlow<String?> = _scannerErrorMessage.asStateFlow()

    private val _recentlySavedPlantId = MutableStateFlow<Long?>(null)
    val recentlySavedPlantId: StateFlow<Long?> = _recentlySavedPlantId.asStateFlow()

    fun navigateTo(destination: AppNavDestination) {
        _currentDestination.value = destination
    }

    fun selectPlant(plant: PlantEntity) {
        _selectedPlant.value = plant
    }

    fun clearSelectedPlant() {
        _selectedPlant.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveFilter(filter: String) {
        _activeFilter.value = filter
    }

    fun analyzePlantBitmap(bitmap: Bitmap, samplePlantName: String? = null) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _scannerErrorMessage.value = null
            _scannedBitmap.value = bitmap
            try {
                val result = if (samplePlantName != null) {
                    // Quick high-accuracy sample match for emulator test presets
                    getPresetPlant(samplePlantName)
                } else {
                    repository.identifyPlant(bitmap)
                }
                _scanResult.value = result
            } catch (e: Exception) {
                Log.e(TAG, "Error analyzing plant", e)
                _scannerErrorMessage.value = "Error al identificar: ${e.localizedMessage ?: "Reintente"}"
                _scanResult.value = getPresetPlant("Basil")
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun saveIdentifiedPlant(customNickname: String? = null) {
        val result = _scanResult.value ?: return
        viewModelScope.launch {
            val plantToSave = if (!customNickname.isNullOrBlank()) {
                result.copy(nickname = customNickname)
            } else result

            val newId = repository.saveIdentifiedPlant(
                result = plantToSave,
                imageResName = getDrawableForPlantName(plantToSave.name)
            )
            _recentlySavedPlantId.value = newId

            // Automatically select and navigate to this new plant
            val newEntity = PlantEntity(
                id = newId,
                name = plantToSave.name,
                nickname = plantToSave.nickname,
                scientificName = plantToSave.scientificName,
                plantType = plantToSave.plantType,
                temperatureRange = plantToSave.temperatureRange,
                harvestSeason = plantToSave.harvestSeason,
                bestPlace = plantToSave.bestPlace,
                waterLevelStatus = plantToSave.waterLevelStatus,
                waterRecommendation = plantToSave.waterRecommendation,
                waterLevelPercent = 25,
                careTips = plantToSave.careTips,
                imageResName = getDrawableForPlantName(plantToSave.name),
                scannedTimestamp = System.currentTimeMillis()
            )
            _selectedPlant.value = newEntity
        }
    }

    fun resetScanner() {
        _scanResult.value = null
        _scannedBitmap.value = null
        _scannerErrorMessage.value = null
    }

    fun toggleFavorite(plant: PlantEntity) {
        viewModelScope.launch {
            val updated = plant.copy(isFavorite = !plant.isFavorite)
            repository.updatePlant(updated)
            if (_selectedPlant.value?.id == plant.id) {
                _selectedPlant.value = updated
            }
        }
    }

    fun markWatered(plant: PlantEntity) {
        viewModelScope.launch {
            val updated = plant.copy(
                waterLevelStatus = "Optimal",
                waterLevelPercent = 85,
                waterRecommendation = "Next watering in 3 days"
            )
            repository.updatePlant(updated)
            if (_selectedPlant.value?.id == plant.id) {
                _selectedPlant.value = updated
            }
        }
    }

    fun deletePlant(plant: PlantEntity) {
        viewModelScope.launch {
            repository.deletePlant(plant)
            if (_selectedPlant.value?.id == plant.id) {
                _selectedPlant.value = null
            }
        }
    }

    private fun getDrawableForPlantName(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("basil") || lower.contains("albahaca") -> "plant_basil"
            lower.contains("lettuce") || lower.contains("lechuga") -> "plant_lettuce"
            lower.contains("rucola") || lower.contains("arugula") || lower.contains("rúcula") -> "plant_rucola"
            lower.contains("tomato") || lower.contains("tomate") -> "plant_tomato"
            else -> "plant_basil"
        }
    }

    private fun getPresetPlant(name: String): PlantIdentificationResult {
        return when (name.lowercase()) {
            "lettuce", "lechuga" -> PlantIdentificationResult(
                name = "Lettuce",
                nickname = "Crispy",
                scientificName = "Lactuca sativa",
                plantType = "Leafy Green Vegetable",
                temperatureRange = "15–22°C",
                harvestSeason = "June - Aug",
                bestPlace = "Partial shade",
                waterLevelStatus = "Optimal",
                waterRecommendation = "Water 20 mL tomorrow morning",
                careTips = "Prefers cool weather. Keep soil evenly moist to prevent bitterness. Harvest outer leaves continuously."
            )
            "rucola", "rúcula", "arugula" -> PlantIdentificationResult(
                name = "Rucola",
                nickname = "Rocket",
                scientificName = "Eruca vesicaria",
                plantType = "Edible Herbaceous Salad Plant",
                temperatureRange = "16–24°C",
                harvestSeason = "May - July",
                bestPlace = "Sunny / Partial shade",
                waterLevelStatus = "Good",
                waterRecommendation = "Water 15 mL every 2 days",
                careTips = "Leaves have a distinctive nutty and peppery flavor. Water regularly to avoid early bolting."
            )
            "tomato", "tomate" -> PlantIdentificationResult(
                name = "Tomato",
                nickname = "Cherry",
                scientificName = "Solanum lycopersicum",
                plantType = "Fruiting Solanaceous Plant",
                temperatureRange = "22–30°C",
                harvestSeason = "July - Aug",
                bestPlace = "Full direct sun",
                waterLevelStatus = "Good",
                waterRecommendation = "Water 35 mL every 2 days",
                careTips = "Needs strong sun and support stakes as fruit develops. Water at soil level to prevent leaf blight."
            )
            else -> PlantIdentificationResult(
                name = "Basil",
                nickname = "Morgan",
                scientificName = "Ocimum basilicum",
                plantType = "Aromatic Culinary Herb",
                temperatureRange = "20–28°C",
                harvestSeason = "May - June",
                bestPlace = "Sunny side",
                waterLevelStatus = "Attention",
                waterRecommendation = "Water me 15 mL today",
                careTips = "Keep soil moist but well-draining. Thrives in warm, sunny locations with 6+ hours of sunlight daily. Pinch off flower buds to encourage bushy foliage growth."
            )
        }
    }
}
