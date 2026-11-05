package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.ai.PlantAiService
import com.example.data.ai.PlantIdentificationResult
import com.example.data.local.PlantDao
import com.example.data.local.PlantEntity
import kotlinx.coroutines.flow.Flow

class PlantRepository(
    private val plantDao: PlantDao,
    private val plantAiService: PlantAiService = PlantAiService()
) {
    val allPlants: Flow<List<PlantEntity>> = plantDao.getAllPlants()

    fun getPlantById(id: Long): Flow<PlantEntity?> = plantDao.getPlantById(id)

    fun searchPlants(query: String): Flow<List<PlantEntity>> = plantDao.searchPlants(query)

    suspend fun insertPlant(plant: PlantEntity): Long = plantDao.insertPlant(plant)

    suspend fun updatePlant(plant: PlantEntity) = plantDao.updatePlant(plant)

    suspend fun deletePlant(plant: PlantEntity) = plantDao.deletePlant(plant)

    suspend fun deleteById(id: Long) = plantDao.deleteById(id)

    suspend fun identifyPlant(bitmap: Bitmap): PlantIdentificationResult {
        return plantAiService.identifyPlant(bitmap)
    }

    suspend fun saveIdentifiedPlant(
        result: PlantIdentificationResult,
        imageUri: String? = null,
        imageResName: String? = null
    ): Long {
        val entity = PlantEntity(
            name = result.name,
            nickname = result.nickname,
            scientificName = result.scientificName,
            plantType = result.plantType,
            temperatureRange = result.temperatureRange,
            harvestSeason = result.harvestSeason,
            bestPlace = result.bestPlace,
            waterLevelStatus = result.waterLevelStatus,
            waterRecommendation = result.waterRecommendation,
            waterLevelPercent = if (result.waterLevelStatus.equals("Attention", ignoreCase = true)) 18 else 55,
            careTips = result.careTips,
            imageUri = imageUri,
            imageResName = imageResName ?: getBestMatchedDrawable(result.name),
            scannedTimestamp = System.currentTimeMillis()
        )
        return plantDao.insertPlant(entity)
    }

    private fun getBestMatchedDrawable(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("basil") || lower.contains("albahaca") -> "plant_basil"
            lower.contains("lettuce") || lower.contains("lechuga") -> "plant_lettuce"
            lower.contains("rucola") || lower.contains("arugula") || lower.contains("rúcula") -> "plant_rucola"
            lower.contains("tomato") || lower.contains("tomate") -> "plant_tomato"
            else -> "plant_basil"
        }
    }
}
