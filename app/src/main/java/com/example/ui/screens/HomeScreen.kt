package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.local.PlantEntity
import com.example.ui.components.AppNavDestination
import com.example.ui.theme.BorderGray
import com.example.ui.theme.CardLavenderPink
import com.example.ui.theme.CardMint
import com.example.ui.theme.CardPeach
import com.example.ui.theme.CardPeriwinkle
import com.example.ui.theme.DarkBotanical
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftGray
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.PlantViewModel

@Composable
fun HomeScreen(
    viewModel: PlantViewModel,
    onNavigateToScan: () -> Unit,
    onOpenPlantDetail: (PlantEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val plants by viewModel.filteredPlants.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()

    var showHelpDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Header: Avatar + Help + Notifications
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .border(1.dp, BorderGray, CircleShape)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=160&auto=format&fit=crop&q=80")
                        .crossfade(true)
                        .build(),
                    contentDescription = "User profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Right icons: Help (?) and Notifications
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { showHelpDialog = true },
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("help_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.HelpOutline,
                        contentDescription = "Help and Guide",
                        tint = TextDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(
                    modifier = Modifier.size(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { /* Notification view */ },
                        modifier = Modifier.testTag("notifications_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = TextDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    // Red/coral indicator dot
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF5252))
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Title: "My plants"
        Text(
            text = "My plants",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            ),
            modifier = Modifier.testTag("title_my_plants")
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Search Bar + Circular (+) Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "Search in your plants",
                        color = TextMuted,
                        fontSize = 15.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Clear search",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BorderGray,
                    unfocusedBorderColor = BorderGray,
                    focusedContainerColor = PureWhite,
                    unfocusedContainerColor = PureWhite,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("search_input")
            )

            // Circular (+) Button
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(DarkBotanical)
                    .clickable { showAddDialog = true }
                    .testTag("add_plant_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add plant",
                    tint = PureWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("All", "Herbs", "Vegetables", "Favorites")
            filters.forEach { filter ->
                val isSelected = activeFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) DarkBotanical else SoftGray)
                        .clickable { viewModel.setActiveFilter(filter) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("filter_$filter"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) PureWhite else TextDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2-Column Grid of Plant Cards
        if (plants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No plants found",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the scanner to identify a plant instantly with camera!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToScan,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBotanical)
                    ) {
                        Text("Open Scanner", color = PureWhite)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(plants, key = { it.id }) { plant ->
                    PlantGridCard(
                        plant = plant,
                        onClick = { onOpenPlantDetail(plant) }
                    )
                }
            }
        }
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Plant Identification Guide") },
            text = {
                Text(
                    "Welcome to My Plants! Point your camera at any plant, leaf, or flower to identify its species instantly. The app shows optimal temperatures, harvest windows, best lighting conditions, and watering schedules, and stores everything in your local database."
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }

    // Add Options Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Plant") },
            text = {
                Text("How would you like to add a new plant to your collection?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAddDialog = false
                        onNavigateToScan()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBotanical)
                ) {
                    Text("Identify with Camera")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PlantGridCard(
    plant: PlantEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Choose pastel background color matching the screenshot
    val cardColor = when {
        plant.name.contains("Lettuce", ignoreCase = true) -> CardLavenderPink
        plant.name.contains("Basil", ignoreCase = true) -> CardMint
        plant.name.contains("Rucola", ignoreCase = true) -> CardPeriwinkle
        plant.name.contains("Tomato", ignoreCase = true) -> CardPeach
        plant.id % 4 == 0L -> CardLavenderPink
        plant.id % 4 == 1L -> CardMint
        plant.id % 4 == 2L -> CardPeriwinkle
        else -> CardPeach
    }

    // In the screenshot, the first card has a dark pill badge, and the others have white badges
    val isDarkBadge = plant.name.contains("Lettuce", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.82f)
            .clip(RoundedCornerShape(26.dp))
            .clickable(onClick = onClick)
            .testTag("plant_card_${plant.id}")
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Botanical Watercolor Illustration
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, bottom = 48.dp, start = 14.dp, end = 14.dp),
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

            // Bottom Floating Pill Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .shadow(elevation = if (isDarkBadge) 0.dp else 2.dp, shape = RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDarkBadge) DarkBotanical else PureWhite)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = plant.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkBadge) PureWhite else TextDark
                            )
                        )
                        Text(
                            text = plant.harvestSeason,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = if (isDarkBadge) Color(0xFFA0ABA4) else TextMuted
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Row of mini icons (sun, water droplet, flower)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WbSunny,
                            contentDescription = "Sun",
                            tint = if (isDarkBadge) PureWhite else TextDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Icon(
                            imageVector = Icons.Filled.WaterDrop,
                            contentDescription = "Water",
                            tint = if (isDarkBadge) PureWhite else TextDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Icon(
                            imageVector = Icons.Filled.Spa,
                            contentDescription = "Care",
                            tint = if (isDarkBadge) PureWhite else TextDark,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}
