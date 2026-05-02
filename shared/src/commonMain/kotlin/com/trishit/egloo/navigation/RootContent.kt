package com.trishit.egloo.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.*
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.trishit.egloo.ui.screens.*
import com.egloo.ui.theme.EglooTheme
import com.trishit.egloo.navigation.toDestination
import com.trishit.egloo.ui.screens.ChatScreen
import com.trishit.egloo.ui.screens.HomeScreen
import com.trishit.egloo.ui.screens.OnboardingScreen
import com.trishit.egloo.ui.screens.SettingsScreen
import com.trishit.egloo.ui.screens.SourcesScreen
import com.trishit.egloo.ui.screens.TopicsScreen

// ── Bottom nav items ──────────────────────────────────────────────────────────

private data class NavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val destination: com.trishit.egloo.navigation.Destination,
)

private val navItems = listOf(
    _root_ide_package_.com.trishit.egloo.navigation.NavItem(
        "Home",
        Icons.Default.Home,
        Icons.Default.Home,
        _root_ide_package_.com.trishit.egloo.navigation.Destination.Home
    ),
    _root_ide_package_.com.trishit.egloo.navigation.NavItem(
        "Chat",
        Icons.Default.Search,
        Icons.Default.Search,
        _root_ide_package_.com.trishit.egloo.navigation.Destination.Chat
    ),
    _root_ide_package_.com.trishit.egloo.navigation.NavItem(
        "Topics",
        Icons.Default.List,
        Icons.Default.List,
        _root_ide_package_.com.trishit.egloo.navigation.Destination.Topics
    ),
    _root_ide_package_.com.trishit.egloo.navigation.NavItem(
        "Sources",
        Icons.Default.AccountBox,
        Icons.Default.AccountBox,
        _root_ide_package_.com.trishit.egloo.navigation.Destination.Sources
    ),
    _root_ide_package_.com.trishit.egloo.navigation.NavItem(
        "Settings",
        Icons.Default.Settings,
        Icons.Default.Settings,
        _root_ide_package_.com.trishit.egloo.navigation.Destination.Settings
    ),
)

// ── Root content — shared across Android, iOS, Desktop ───────────────────────

@Composable
fun RootContent(component: com.trishit.egloo.navigation.RootComponent, darkTheme: Boolean = true) {
    _root_ide_package_.com.egloo.ui.theme.EglooTheme(darkTheme = darkTheme) {
        val stack by component.stack.subscribeAsState()
        val activeChild = stack.active.instance

        // Show onboarding without chrome
        if (activeChild is com.trishit.egloo.navigation.RootComponent.Child.OnboardingChild) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                _root_ide_package_.com.trishit.egloo.ui.screens.OnboardingScreen(
                    onComplete = { component.navigateTo(_root_ide_package_.com.trishit.egloo.navigation.Destination.Home) }
                )
            }
            return@EglooTheme
        }

        // Main app shell
        Scaffold(
            bottomBar = {
                _root_ide_package_.com.trishit.egloo.navigation.EglooBottomBar(
                    activeDestination = activeChild.toDestination(),
                    onNavigate = com.trishit.egloo.navigation.RootComponent::navigateTo,
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                Children(
                    stack = stack,
                    animation = stackAnimation(fade() + scale()),
                ) { child ->
                    when (val instance = child.instance) {
                        is com.trishit.egloo.navigation.RootComponent.Child.HomeChild -> _root_ide_package_.com.trishit.egloo.ui.screens.HomeScreen()
                        is com.trishit.egloo.navigation.RootComponent.Child.ChatChild -> _root_ide_package_.com.trishit.egloo.ui.screens.ChatScreen()
                        is com.trishit.egloo.navigation.RootComponent.Child.TopicsChild -> _root_ide_package_.com.trishit.egloo.ui.screens.TopicsScreen()
                        is com.trishit.egloo.navigation.RootComponent.Child.SourcesChild -> _root_ide_package_.com.trishit.egloo.ui.screens.SourcesScreen()
                        is com.trishit.egloo.navigation.RootComponent.Child.SettingsChild -> _root_ide_package_.com.trishit.egloo.ui.screens.SettingsScreen()
                        is com.trishit.egloo.navigation.RootComponent.Child.OnboardingChild -> {} // handled above
                    }
                }
            }
        }
    }
}

// ── Bottom navigation bar ─────────────────────────────────────────────────────

@Composable
private fun EglooBottomBar(
    activeDestination: com.trishit.egloo.navigation.Destination?,
    onNavigate: (com.trishit.egloo.navigation.Destination) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        _root_ide_package_.com.trishit.egloo.navigation.navItems.forEach { item ->
            val isSelected = activeDestination == item.destination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                    )
                },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

// ── Desktop nav rail (used by desktopApp instead of bottom bar) ───────────────

@Composable
fun EglooNavRail(
    activeDestination: com.trishit.egloo.navigation.Destination?,
    onNavigate: (com.trishit.egloo.navigation.Destination) -> Unit,
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Spacer(Modifier.height(16.dp))
            // Pingo wordmark
            Text(
                "eg",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(8.dp))
        }
    ) {
        Spacer(Modifier.weight(1f))
        _root_ide_package_.com.trishit.egloo.navigation.navItems.forEach { item ->
            val isSelected = activeDestination == item.destination
            NavigationRailItem(
                selected = isSelected,
                onClick = { onNavigate(item.destination) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

// ── Desktop root content (nav rail layout) ────────────────────────────────────

@Composable
fun DesktopRootContent(
    component: com.trishit.egloo.navigation.RootComponent,
    darkTheme: Boolean = true
) {
    _root_ide_package_.com.egloo.ui.theme.EglooTheme(darkTheme = darkTheme) {
        val stack by component.stack.subscribeAsState()
        val activeChild = stack.active.instance

        if (activeChild is com.trishit.egloo.navigation.RootComponent.Child.OnboardingChild) {
            Surface(Modifier.fillMaxSize(), color = Color.Transparent) {
                _root_ide_package_.com.trishit.egloo.ui.screens.OnboardingScreen(onComplete = {
                    component.navigateTo(
                        _root_ide_package_.com.trishit.egloo.navigation.Destination.Home
                    )
                })
            }
            return@EglooTheme
        }

        // Color.Transparent is required — any opaque fill paints over the Mica/
        // Acrylic layer the OS renders behind the window. Individual surfaces
        // (NavRail, content cards) keep their own MaterialTheme surface colors.
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                _root_ide_package_.com.trishit.egloo.navigation.EglooNavRail(
                    activeDestination = activeChild.toDestination(),
                    onNavigate = com.trishit.egloo.navigation.RootComponent::navigateTo,
                )

                VerticalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.fillMaxHeight(),
                )

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Children(
                        stack = stack,
                        animation = stackAnimation(fade()),
                    ) { child ->
                        when (child.instance) {
                            is com.trishit.egloo.navigation.RootComponent.Child.HomeChild -> _root_ide_package_.com.trishit.egloo.ui.screens.HomeScreen()
                            is com.trishit.egloo.navigation.RootComponent.Child.ChatChild -> _root_ide_package_.com.trishit.egloo.ui.screens.ChatScreen()
                            is com.trishit.egloo.navigation.RootComponent.Child.TopicsChild -> _root_ide_package_.com.trishit.egloo.ui.screens.TopicsScreen()
                            is com.trishit.egloo.navigation.RootComponent.Child.SourcesChild -> _root_ide_package_.com.trishit.egloo.ui.screens.SourcesScreen()
                            is com.trishit.egloo.navigation.RootComponent.Child.SettingsChild -> _root_ide_package_.com.trishit.egloo.ui.screens.SettingsScreen()
                            is com.trishit.egloo.navigation.RootComponent.Child.OnboardingChild -> {}
                        }
                    }
                }
            }
        }
    }
}

// ── Helper extension ──────────────────────────────────────────────────────────

private fun com.trishit.egloo.navigation.RootComponent.Child.toDestination(): com.trishit.egloo.navigation.Destination? =
    when (this) {
        is com.trishit.egloo.navigation.RootComponent.Child.HomeChild -> _root_ide_package_.com.trishit.egloo.navigation.Destination.Home
        is com.trishit.egloo.navigation.RootComponent.Child.ChatChild -> _root_ide_package_.com.trishit.egloo.navigation.Destination.Chat
        is com.trishit.egloo.navigation.RootComponent.Child.TopicsChild -> _root_ide_package_.com.trishit.egloo.navigation.Destination.Topics
        is com.trishit.egloo.navigation.RootComponent.Child.SourcesChild -> _root_ide_package_.com.trishit.egloo.navigation.Destination.Sources
        is com.trishit.egloo.navigation.RootComponent.Child.SettingsChild -> _root_ide_package_.com.trishit.egloo.navigation.Destination.Settings
        is com.trishit.egloo.navigation.RootComponent.Child.OnboardingChild -> null
    }
