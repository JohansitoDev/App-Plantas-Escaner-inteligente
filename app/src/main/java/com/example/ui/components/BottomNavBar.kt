package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkBotanical
import com.example.ui.theme.NeonScanGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted

enum class AppNavDestination {
    HOME,
    CALENDAR,
    SCANNER,
    HISTORY,
    SETTINGS
}

@Composable
fun BotanicalBottomBar(
    currentDestination: AppNavDestination,
    onNavigate: (AppNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            NavBarIcon(
                icon = if (currentDestination == AppNavDestination.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                isSelected = currentDestination == AppNavDestination.HOME,
                onClick = { onNavigate(AppNavDestination.HOME) },
                contentDescription = "Home",
                tag = "nav_home"
            )

            // Calendar
            NavBarIcon(
                icon = if (currentDestination == AppNavDestination.CALENDAR) Icons.Filled.Today else Icons.Outlined.Today,
                isSelected = currentDestination == AppNavDestination.CALENDAR,
                onClick = { onNavigate(AppNavDestination.CALENDAR) },
                contentDescription = "Calendar",
                tag = "nav_calendar"
            )

            // Center Scanner Button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(DarkBotanical)
                    .clickable { onNavigate(AppNavDestination.SCANNER) }
                    .testTag("nav_scanner"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (currentDestination == AppNavDestination.SCANNER) DarkBotanical else Color(0xFF222B25)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CropFree,
                        contentDescription = "Scanner",
                        tint = if (currentDestination == AppNavDestination.SCANNER) NeonScanGreen else PureWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // History / Catalog
            NavBarIcon(
                icon = if (currentDestination == AppNavDestination.HISTORY) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                isSelected = currentDestination == AppNavDestination.HISTORY,
                onClick = { onNavigate(AppNavDestination.HISTORY) },
                contentDescription = "History",
                tag = "nav_history"
            )

            // Settings
            NavBarIcon(
                icon = if (currentDestination == AppNavDestination.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                isSelected = currentDestination == AppNavDestination.SETTINGS,
                onClick = { onNavigate(AppNavDestination.SETTINGS) },
                contentDescription = "Settings",
                tag = "nav_settings"
            )
        }
    }
}

@Composable
private fun NavBarIcon(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    tag: String
) {
    val tint = animateColorAsState(
        targetValue = if (isSelected) DarkBotanical else TextMuted,
        label = "tintAnimation"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEBEFEA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = tint.value,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint.value,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
