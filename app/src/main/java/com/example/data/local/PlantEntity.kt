package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val nickname: String,
    val scientificName: String,
    val plantType: String,
    val temperatureRange: String,
    val harvestSeason: String,
    val bestPlace: String,
    val waterLevelStatus: String, // "Attention", "Optimal", "Good", etc.
    val waterRecommendation: String, // e.g., "Water me 15 mL today"
    val waterLevelPercent: Int = 30, // 0 to 100 for graph display
    val careTips: String,
    val imageResName: String? = null, // e.g. "plant_basil", "plant_lettuce"
    val imageUri: String? = null,
    val scannedTimestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val notes: String = ""
)
