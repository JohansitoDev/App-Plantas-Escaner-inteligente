package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [PlantEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plant_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialPlants(database.plantDao())
                    }
                }
            }
        }

        suspend fun populateInitialPlants(dao: PlantDao) {
            val initialPlants = listOf(
                PlantEntity(
                    name = "Lettuce",
                    nickname = "Crispy",
                    scientificName = "Lactuca sativa",
                    plantType = "Leafy Green Vegetable",
                    temperatureRange = "15–22°C",
                    harvestSeason = "June - Aug",
                    bestPlace = "Partial shade",
                    waterLevelStatus = "Optimal",
                    waterRecommendation = "Water 20 mL tomorrow morning",
                    waterLevelPercent = 65,
                    careTips = "Prefers cool temperatures and consistently moist, well-draining soil. Harvest outer leaves first.",
                    imageResName = "plant_lettuce",
                    isFavorite = false,
                    scannedTimestamp = System.currentTimeMillis() - 86400000L * 4
                ),
                PlantEntity(
                    name = "Basil",
                    nickname = "Morgan",
                    scientificName = "Ocimum basilicum",
                    plantType = "Aromatic Culinary Herb",
                    temperatureRange = "20–28°C",
                    harvestSeason = "May - June",
                    bestPlace = "Sunny side",
                    waterLevelStatus = "Attention",
                    waterRecommendation = "Water me 15 mL today",
                    waterLevelPercent = 18,
                    careTips = "Keep soil moist but not waterlogged. Pinch flowers as soon as they appear to encourage lush foliage.",
                    imageResName = "plant_basil",
                    isFavorite = true,
                    scannedTimestamp = System.currentTimeMillis() - 86400000L * 2
                ),
                PlantEntity(
                    name = "Rucola",
                    nickname = "Rocket",
                    scientificName = "Eruca vesicaria",
                    plantType = "Edible Herbaceous Plant",
                    temperatureRange = "16–24°C",
                    harvestSeason = "May - July",
                    bestPlace = "Sunny / Partial shade",
                    waterLevelStatus = "Good",
                    waterRecommendation = "Water 15 mL every 2 days",
                    waterLevelPercent = 50,
                    careTips = "Peppery flavor intensifies with sunlight. Keep roots cool with organic mulch.",
                    imageResName = "plant_rucola",
                    isFavorite = false,
                    scannedTimestamp = System.currentTimeMillis() - 86400000L * 3
                ),
                PlantEntity(
                    name = "Tomato",
                    nickname = "Cherry",
                    scientificName = "Solanum lycopersicum",
                    plantType = "Fruiting Vine Plant",
                    temperatureRange = "22–30°C",
                    harvestSeason = "July - Aug",
                    bestPlace = "Full direct sun",
                    waterLevelStatus = "Good",
                    waterRecommendation = "Water 35 mL every 2 days",
                    waterLevelPercent = 45,
                    careTips = "Needs at least 6-8 hours of direct sunlight. Water at the base without wetting foliage to avoid fungus.",
                    imageResName = "plant_tomato",
                    isFavorite = true,
                    scannedTimestamp = System.currentTimeMillis() - 86400000L * 1
                )
            )
            dao.insertAll(initialPlants)
        }
    }
}
