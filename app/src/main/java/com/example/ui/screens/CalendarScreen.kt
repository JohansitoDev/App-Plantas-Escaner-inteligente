package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlantEntity
import com.example.ui.theme.DarkBotanical
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftGray
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WaterAlertCoral
import com.example.ui.viewmodel.PlantViewModel

@Composable
fun CalendarScreen(
    viewModel: PlantViewModel,
    onSelectPlant: (PlantEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val plants by viewModel.allPlants.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Care Schedule",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Daily hydration and care reminders for your garden.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = TextMuted
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Days of week horizontal pill row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val days = listOf("Mon" to "3", "Tue" to "4", "Wed" to "5", "Thu" to "6", "Fri" to "7", "Sat" to "8", "Sun" to "9")
            days.forEachIndexed { index, (day, date) ->
                val isToday = index == 2 // Wed 5
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isToday) DarkBotanical else PureWhite)
                        .padding(horizontal = 10.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = day,
                        fontSize = 11.sp,
                        color = if (isToday) PureWhite.copy(alpha = 0.8f) else TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday) PureWhite else TextDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Today's Tasks",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(plants, key = { it.id }) { plant ->
                CareTaskCard(
                    plant = plant,
                    onWater = { viewModel.markWatered(plant) },
                    onClick = { onSelectPlant(plant) }
                )
            }
        }
    }
}

@Composable
fun CareTaskCard(
    plant: PlantEntity,
    onWater: () -> Unit,
    onClick: () -> Unit
) {
    val isAttention = plant.waterLevelStatus.equals("Attention", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("care_task_${plant.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isAttention) Color(0xFFFFECEF) else Color(0xFFE8F5EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = if (isAttention) WaterAlertCoral else DarkBotanical,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "${plant.name} (${plant.nickname})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    )
                    Text(
                        text = plant.waterRecommendation,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = if (isAttention) WaterAlertCoral else TextMuted,
                            fontWeight = if (isAttention) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isAttention) DarkBotanical else Color(0xFFE8EFEA))
                    .clickable { onWater() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Mark done",
                    tint = if (isAttention) PureWhite else DarkBotanical,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
