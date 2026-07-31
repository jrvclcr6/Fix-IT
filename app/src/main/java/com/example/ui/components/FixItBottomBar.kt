package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.navigation.Screen
import com.example.ui.theme.FixItBluePrimary

@Composable
fun FixItBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavigationItem("Home", Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
        NavigationItem("Bookmarks", Screen.Bookmarks.route, Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder, "nav_bookmarks"),
        NavigationItem("History", Screen.History.route, Icons.Filled.History, Icons.Outlined.History, "nav_history"),
        NavigationItem("About", Screen.About.route, Icons.Filled.Info, Icons.Outlined.Info, "nav_about")
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF001D36)
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF001D36),
                    selectedTextColor = Color(0xFF001D36),
                    indicatorColor = Color(0xFFD3E4FF),
                    unselectedIconColor = Color(0xFF43474E),
                    unselectedTextColor = Color(0xFF43474E)
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}

private data class NavigationItem(
    val label: String,
    val route: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)
