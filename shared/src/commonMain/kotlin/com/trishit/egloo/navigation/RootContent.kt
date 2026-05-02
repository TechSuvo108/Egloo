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
import com.trishit.egloo.ui.theme.EglooTheme

// ── Bottom nav items ──────────────────────────────────────────────────────────

private data class NavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val destination: Destination,
)

private val navItems = listOf(
    NavItem(
        "Home",
        Icons.Default.Home,
        Icons.Default.Home,
        Destination.Home
    ),
    NavItem(
        "Chat",
        Icons.Default.Search,
        Icons.Default.Search,
        Destination.Chat
    ),
    NavItem(
        "Topics",
        Icons.Default.List,
        Icons.Default.List,
        Destination.Topics
    ),
    NavItem(
        "Sources",
        Icons.Default.AccountBox,
        Icons.Default.AccountBox,
        Destination.Sources
    ),
    NavItem(
        "Settings",
        Icons.Default.Settings,
        Icons.Default.Settings,
        Destination.Settings
    ),
)

// ── Root content — shared across Android, iOS, Desktop ───────────────────────

@Composable
fun RootContent(component: RootComponent, darkTheme: Boolean = true) {
    EglooTheme(darkTheme = darkTheme) {
        val stack by component.stack.subscribeAsState()
        val activeChild = stack.active.instance

        // Show onboarding without chrome
        if (activeChild is RootComponent.Child.OnboardingChild) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                OnboardingScreen(
                    onComplete = { component.navigateTo(Destination.Home) }
                )
            }
            return@EglooTheme
        }

        // Main app shell
        Scaffold(
            bottomBar = {
                EglooBottomBar(
                    activeDestination = activeChild.toDestination(),
                    onNavigate = component::navigateTo,
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
                        is RootComponent.Child.HomeChild -> HomeScreen()
                        is RootComponent.Child.ChatChild -> ChatScreen()
                        is RootComponent.Child.TopicsChild -> TopicsScreen()
                        is RootComponent.Child.SourcesChild -> SourcesScreen()
                        is RootComponent.Child.SettingsChild -> SettingsScreen()
                        is RootComponent.Child.OnboardingChild -> {} // handled above
                    }
                }
            }
        }
    }
}

// ── Bottom navigation bar ─────────────────────────────────────────────────────

@Composable
private fun EglooBottomBar(
    activeDestination: Destination?,
    onNavigate: (Destination) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        navItems.forEach { item ->
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
    activeDestination: Destination?,
    onNavigate: (Destination) -> Unit,
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
        navItems.forEach { item ->
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
    component: RootComponent,
    darkTheme: Boolean = true
) {
    EglooTheme(darkTheme = darkTheme) {
        val stack by component.stack.subscribeAsState()
        val activeChild = stack.active.instance

        if (activeChild is RootComponent.Child.OnboardingChild) {
            Surface(Modifier.fillMaxSize(), color = Color.Transparent) {
                OnboardingScreen(onComplete = {
                    component.navigateTo(Destination.Home)
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
                EglooNavRail(
                    activeDestination = activeChild.toDestination(),
                    onNavigate = component::navigateTo,
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
                            is RootComponent.Child.HomeChild -> HomeScreen()
                            is RootComponent.Child.ChatChild -> ChatScreen()
                            is RootComponent.Child.TopicsChild -> TopicsScreen()
                            is RootComponent.Child.SourcesChild -> SourcesScreen()
                            is RootComponent.Child.SettingsChild -> SettingsScreen()
                            is RootComponent.Child.OnboardingChild -> {}
                        }
                    }
                }
            }
        }
    }
}

// ── Helper extension ──────────────────────────────────────────────────────────

private fun RootComponent.Child.toDestination(): Destination? =
    when (this) {
        is RootComponent.Child.HomeChild -> Destination.Home
        is RootComponent.Child.ChatChild -> Destination.Chat
        is RootComponent.Child.TopicsChild -> Destination.Topics
        is RootComponent.Child.SourcesChild -> Destination.Sources
        is RootComponent.Child.SettingsChild -> Destination.Settings
        is RootComponent.Child.OnboardingChild -> null
    }
