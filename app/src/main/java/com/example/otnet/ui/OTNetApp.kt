package com.example.otnet.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.otnet.ui.browse.BrowseScreen
import com.example.otnet.ui.browse.CategoryDetailScreen
import com.example.otnet.ui.detail.ContentDetailScreen
import com.example.otnet.ui.home.HomeScreen
import com.example.otnet.ui.player.PlayerScreen
import com.example.otnet.ui.theme.OTNetBackground
import com.example.otnet.ui.theme.OTNetCard
import com.example.otnet.ui.theme.OTNetPrimary
import com.example.otnet.ui.theme.OTNetTextSecondary

@Composable
fun OTNetApp() {
    val nav = rememberNavController()
    val currentEntry by nav.currentBackStackEntryAsState()
    val route = currentEntry?.destination?.route
    val immersive = route?.startsWith("player/") == true

    Scaffold(
        bottomBar = { if (!immersive) OTNetBottomBar(nav) },
        containerColor = OTNetBackground,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            NavHost(
                navController = nav,
                startDestination = "home",
            ) {
                composable("home") {
                    HomeScreen(onContentTap = { id -> nav.navigate("content/$id") })
                }
                composable("browse") {
                    BrowseScreen(onCategoryTap = { id -> nav.navigate("category/$id") })
                }
                composable("category/{id}") { entry ->
                    CategoryDetailScreen(
                        categoryId = entry.arguments?.getString("id").orEmpty(),
                        onContentTap = { id -> nav.navigate("content/$id") },
                        onBack = { nav.popBackStack() },
                    )
                }
                composable("content/{id}") { entry ->
                    val id = entry.arguments?.getString("id").orEmpty()
                    ContentDetailScreen(
                        contentId = id,
                        onPlay = { mediaIndex -> nav.navigate("player/$id/$mediaIndex") },
                        onBack = { nav.popBackStack() },
                    )
                }
                composable("player/{id}/{mediaIndex}") { entry ->
                    PlayerScreen(
                        contentId = entry.arguments?.getString("id").orEmpty(),
                        mediaIndex = entry.arguments?.getString("mediaIndex")?.toIntOrNull() ?: 0,
                        onClose = { nav.popBackStack() },
                    )
                }
            }
        }
    }
}

@Composable
private fun OTNetBottomBar(nav: NavHostController) {
    val current by nav.currentBackStackEntryAsState()
    val route = current?.destination?.route

    NavigationBar(
        containerColor = OTNetCard,
        contentColor = OTNetTextSecondary,
        tonalElevation = 0.dp,
    ) {
        val items = listOf(
            Triple("home", "Home", Icons.Outlined.Home),
            Triple("browse", "Browse", Icons.Outlined.GridView),
        )
        items.forEach { (dest, label, icon) ->
            val selected = route == dest ||
                (route?.startsWith("category/") == true && dest == "browse") ||
                (route?.startsWith("content/") == true && dest == "home")
            NavigationBarItem(
                selected = selected,
                onClick = {
                    nav.navigate(dest) {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OTNetPrimary,
                    selectedTextColor = OTNetPrimary,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = OTNetTextSecondary,
                    unselectedTextColor = OTNetTextSecondary,
                ),
            )
        }
    }
}
