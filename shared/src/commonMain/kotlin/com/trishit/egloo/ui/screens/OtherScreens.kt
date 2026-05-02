package com.trishit.egloo.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import com.trishit.egloo.domain.models.*
import com.trishit.egloo.domain.viewmodels.*
import com.trishit.egloo.ui.components.*
import com.trishit.egloo.ui.theme.EglooColors
import com.trishit.egloo.domain.models.Topic
import com.trishit.egloo.domain.viewmodels.SettingsViewModel
import com.trishit.egloo.domain.viewmodels.SourcesViewModel
import com.trishit.egloo.domain.viewmodels.TopicsViewModel
import com.trishit.egloo.ui.components.SourceBadge
import com.trishit.egloo.ui.components.SourceDot
import com.trishit.egloo.ui.components.timeAgo
import com.trishit.egloo.ui.screens.toColor
import org.koin.compose.koinInject

// =============================================================================
// TOPICS SCREEN
// =============================================================================

@Composable
fun TopicsScreen(viewModel: com.trishit.egloo.domain.viewmodels.TopicsViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
            Text("Topics", style = MaterialTheme.typography.displaySmall)
            Text(
                "${state.topics.size} clusters from your knowledge base",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.topics) { topic ->
                    _root_ide_package_.com.trishit.egloo.ui.screens.TopicCard(
                        topic = topic,
                        onClick = { viewModel.selectTopic(topic) })
                }
            }
        }
    }

    // Topic detail bottom sheet
    state.selectedTopic?.let { topic ->
        _root_ide_package_.com.trishit.egloo.ui.screens.TopicDetailSheet(
            topic = topic,
            onDismiss = { viewModel.selectTopic(null) },
        )
    }
}

@Composable
private fun TopicCard(topic: com.trishit.egloo.domain.models.Topic, onClick: () -> Unit) {
    val accentColor = topic.color.toColor()
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = accentColor.copy(alpha = 0.12f),
        modifier = Modifier.fillMaxWidth().aspectRatio(1.1f),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Source dots
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                topic.sources.take(3).forEach {
                    _root_ide_package_.com.trishit.egloo.ui.components.SourceDot(
                        it
                    )
                }
            }

            Column {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${topic.itemCount} items",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                )
                Text(
                    text = "Updated ${topic.lastUpdatedAt.timeAgo()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicDetailSheet(topic: com.trishit.egloo.domain.models.Topic, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(topic.title, style = MaterialTheme.typography.headlineMedium)
            Text(topic.summary, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                topic.sources.forEach {
                    _root_ide_package_.com.trishit.egloo.ui.components.SourceBadge(
                        it
                    )
                }
            }
            Text(
                "${topic.itemCount} items · Updated ${topic.lastUpdatedAt.timeAgo()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

private fun TopicColor.toColor(): Color = when (this) {
    TopicColor.TEAL   -> _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.TealPrimary
    TopicColor.BLUE   -> _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.BlueAccent
    TopicColor.AMBER  -> _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.BeakAmber
    TopicColor.CORAL  -> Color(0xFFD85A30)
    TopicColor.PURPLE -> Color(0xFF7F77DD)
}

// =============================================================================
// SOURCES SCREEN
// =============================================================================

@Composable
fun SourcesScreen(viewModel: com.trishit.egloo.domain.viewmodels.SourcesViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Sources", style = MaterialTheme.typography.displaySmall)
                Text(
                    "Connect your tools so Pingo can read them",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item { Spacer(Modifier.height(8.dp)) }

        items(state.sources) { source ->
            _root_ide_package_.com.trishit.egloo.ui.screens.SourceRow(
                source = source,
                isConnecting = state.connectingType == source.type,
                onConnect = { viewModel.connectSource(source.type) },
                onDisconnect = { viewModel.disconnectSource(source.id) },
            )
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun SourceRow(
    source: ConnectedSource,
    isConnecting: Boolean,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            _root_ide_package_.com.trishit.egloo.ui.components.SourceDot(
                source.type,
                Modifier.size(10.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = source.type.displayName,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = if (source.isConnected) {
                        "${source.itemCount} items · synced ${source.lastSyncedAt?.timeAgo() ?: "never"}"
                    } else {
                        source.accountName
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (isConnecting) {
                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
            } else if (source.isConnected) {
                OutlinedButton(
                    onClick = onDisconnect,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text("Disconnect", style = MaterialTheme.typography.labelMedium)
                }
            } else {
                Button(
                    onClick = onConnect,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text("Connect", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

// =============================================================================
// SETTINGS SCREEN
// =============================================================================

@Composable
fun SettingsScreen(viewModel: com.trishit.egloo.domain.viewmodels.SettingsViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()
    val settings = state.settings

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.displaySmall)
        }

        item {
            _root_ide_package_.com.trishit.egloo.ui.screens.SettingsSection("Appearance") {
                _root_ide_package_.com.trishit.egloo.ui.screens.ToggleRow(
                                    label = "Dark theme",
                                    description = "Night mode — Pingo prefers the dark",
                                    checked = settings.darkTheme,
                                    onCheckedChange = com.trishit.egloo.domain.viewmodels.SettingsViewModel::toggleDarkTheme,
                                )
            }
        }

        item {
            _root_ide_package_.com.trishit.egloo.ui.screens.SettingsSection("Pingo") {
                _root_ide_package_.com.trishit.egloo.ui.screens.ToggleRow(
                                    label = "Morning greetings",
                                    description = "Pingo says hi when you open the app",
                                    checked = settings.pingoGreetingsEnabled,
                                    onCheckedChange = com.trishit.egloo.domain.viewmodels.SettingsViewModel::togglePingoGreetings,
                                )
            }
        }

        item {
            _root_ide_package_.com.trishit.egloo.ui.screens.SettingsSection("Sync") {
                _root_ide_package_.com.trishit.egloo.ui.screens.ToggleRow(
                                    label = "Digest notifications",
                                    description = "Get notified when your daily digest is ready",
                                    checked = settings.digestNotificationsEnabled,
                                    onCheckedChange = com.trishit.egloo.domain.viewmodels.SettingsViewModel::toggleDigestNotifications,
                                )
                Spacer(Modifier.height(8.dp))
                _root_ide_package_.com.trishit.egloo.ui.screens.SyncFrequencyRow(
                                    hours = settings.syncFrequencyHours,
                                    onSelect = com.trishit.egloo.domain.viewmodels.SettingsViewModel::setSyncFrequency,
                                )
            }
        }

        item {
            if (state.isSaved) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.TealSurface,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Check, null, tint = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.TealDark)
                        Text("Settings saved", color = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.TealDark)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimary),
        )
    }
}

@Composable
private fun SyncFrequencyRow(hours: Int, onSelect: (Int) -> Unit) {
    Column {
        Text("Sync every", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1, 3, 6, 12, 24).forEach { h ->
                FilterChip(
                    selected = hours == h,
                    onClick = { onSelect(h) },
                    label = { Text("${h}h") },
                )
            }
        }
    }
}
