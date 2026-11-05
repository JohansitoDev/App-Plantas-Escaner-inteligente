package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.PlantEntity
import com.example.ui.theme.BorderGray
import com.example.ui.theme.CardMint
import com.example.ui.theme.DarkBotanical
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftGray
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WaterAlertBg
import com.example.ui.theme.WaterAlertCoral
import com.example.ui.viewmodel.PlantViewModel

@Composable
fun PlantDetailScreen(
    plant: PlantEntity,
    viewModel: PlantViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val scrollState = rememberScrollState()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showWaterSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Header: Back Button (<) + Plant Title + Edit (Pencil)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PureWhite)
                    .clickable { onBack() }
                    .testTag("back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = plant.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                ),
                modifier = Modifier.testTag("detail_plant_title")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { viewModel.toggleFavorite(plant) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (plant.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (plant.isFavorite) Color(0xFFFF5252) else TextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PureWhite)
                        .clickable { showDeleteConfirm = true }
                        .testTag("delete_plant_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Delete",
                        tint = TextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section "About"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "About",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            )

            // Mini quick icons on right (Sun, Droplet, Flower/Settings)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.WbSunny,
                    contentDescription = "Sun requirement",
                    tint = TextDark,
                    modifier = Modifier.size(16.dp)
                )
                Icon(
                    imageVector = Icons.Filled.WaterDrop,
                    contentDescription = "Water requirement",
                    tint = TextDark,
                    modifier = Modifier.size(16.dp)
                )
                Icon(
                    imageVector = Icons.Filled.Spa,
                    contentDescription = "Botanical care",
                    tint = TextDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Two-column section: Left big illustration card + Right 4 attributes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Left: Plant Watercolor Illustration in Card
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = CardMint),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .weight(1.1f)
                    .aspectRatio(0.85f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val drawableRes = when (plant.imageResName) {
                        "plant_lettuce" -> R.drawable.plant_lettuce
                        "plant_basil" -> R.drawable.plant_basil
                        "plant_rucola" -> R.drawable.plant_rucola
                        "plant_tomato" -> R.drawable.plant_tomato
                        else -> when {
                            plant.name.contains("Lettuce", true) -> R.drawable.plant_lettuce
                            plant.name.contains("Rucola", true) || plant.name.contains("Arugula", true) -> R.drawable.plant_rucola
                            plant.name.contains("Tomato", true) -> R.drawable.plant_tomato
                            else -> R.drawable.plant_basil
                        }
                    }

                    Image(
                        painter = painterResource(id = drawableRes),
                        contentDescription = plant.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Right: 4 Attribute Tiles
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AttributeTile(value = plant.nickname.ifBlank { plant.name }, label = "Name")
                Spacer(modifier = Modifier.height(10.dp))
                AttributeTile(value = plant.temperatureRange, label = "Temperature")
                Spacer(modifier = Modifier.height(10.dp))
                AttributeTile(value = plant.harvestSeason, label = "Harvest")
                Spacer(modifier = Modifier.height(10.dp))
                AttributeTile(value = plant.bestPlace, label = "Best place")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section "Water level" Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header: "Water level" + "Attention" pill badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Water level",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    )

                    val isAttention = plant.waterLevelStatus.equals("Attention", ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isAttention) WaterAlertCoral else Color(0xFF4ADE80))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isAttention) Icons.Filled.Bolt else Icons.Filled.Check,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = plant.waterLevelStatus,
                                color = PureWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Smooth Wave Chart with gradient fill & nodes
                WaterLevelWaveChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Footer: "Water me 15 mL today" + Quick Water Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = plant.waterRecommendation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    )

                    Button(
                        onClick = {
                            viewModel.markWatered(plant)
                            showWaterSuccess = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBotanical),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("water_now_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Filled.WaterDrop,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Water",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botanical Classification & Characteristics
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Botanical Details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Scientific Name: ", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextMuted)
                    Text(text = plant.scientificName, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontSize = 13.sp, color = TextDark)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Plant Type: ", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextMuted)
                    Text(text = plant.plantType, fontSize = 13.sp, color = TextDark)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Care & Growth Advice", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plant.careTips,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Plant") },
            text = { Text("Are you sure you want to remove ${plant.name} from your collection?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deletePlant(plant)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Delete", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AttributeTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PureWhite)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                color = TextMuted
            )
        )
    }
}

@Composable
private fun WaterLevelWaveChart(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val p1 = Offset(0f, height * 0.75f)
        val p2 = Offset(width * 0.15f, height * 0.35f)
        val p3 = Offset(width * 0.55f, height * 0.55f)
        val p4 = Offset(width * 0.90f, height * 0.85f)

        // Curve path
        val strokePath = Path().apply {
            moveTo(p1.x, p1.y)
            cubicTo(
                width * 0.08f, height * 0.50f,
                width * 0.10f, height * 0.35f,
                p2.x, p2.y
            )
            cubicTo(
                width * 0.30f, height * 0.35f,
                width * 0.40f, height * 0.55f,
                p3.x, p3.y
            )
            cubicTo(
                width * 0.70f, height * 0.55f,
                width * 0.80f, height * 0.85f,
                p4.x, p4.y
            )
        }

        // Fill path with gradient
        val fillPath = Path().apply {
            addPath(strokePath)
            lineTo(p4.x, height)
            lineTo(p1.x, height)
            close()
        }

        // Draw soft pink/coral gradient
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFB3C1).copy(alpha = 0.55f),
                    Color(0xFFFFECEF).copy(alpha = 0.05f)
                ),
                startY = 0f,
                endY = height
            )
        )

        // Draw curve stroke
        drawPath(
            path = strokePath,
            color = Color(0xFFFF6584),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw Node 1 (Historical)
        drawCircle(
            color = Color(0xFFFF6584),
            radius = 4.dp.toPx(),
            center = p2
        )
        drawCircle(
            color = PureWhite,
            radius = 2.dp.toPx(),
            center = p2
        )

        // Draw Node 2 (Today)
        drawCircle(
            color = Color(0xFFFF6584),
            radius = 5.dp.toPx(),
            center = p4
        )
        drawCircle(
            color = PureWhite,
            radius = 2.5.dp.toPx(),
            center = p4
        )
    }

    // Chart Node Labels
    Box(modifier = modifier) {
        // Label for first node "Wed 5/06"
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PureWhite.copy(alpha = 0.9f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Wed 5/06",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted
            )
        }

        // Label for second node "Today"
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 22.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PureWhite.copy(alpha = 0.9f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Today",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6584)
            )
        }
    }
}
