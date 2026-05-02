package com.trishit.egloo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import com.trishit.egloo.domain.models.*
import com.trishit.egloo.domain.viewmodels.*
import com.trishit.egloo.ui.components.*
import com.trishit.egloo.domain.models.ChatMessage
import com.trishit.egloo.domain.viewmodels.ChatViewModel
import com.trishit.egloo.ui.components.PingoAvatar
import com.trishit.egloo.ui.components.SourceBadge
import org.koin.compose.koinInject

@Composable
fun ChatScreen(viewModel: com.trishit.egloo.domain.viewmodels.ChatViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Header ─────────────────────────────────────────────────────────
        _root_ide_package_.com.trishit.egloo.ui.screens.ChatHeader(onClear = com.trishit.egloo.domain.viewmodels.ChatViewModel::clearChat)

        // ── Messages ───────────────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            if (state.messages.isEmpty()) {
                _root_ide_package_.com.trishit.egloo.ui.screens.ChatEmptyState()
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.messages, key = { it.id }) { message ->
                        _root_ide_package_.com.trishit.egloo.ui.screens.MessageBubble(message)
                    }
                    item { Spacer(Modifier.height(60.dp)) }
                }
            }
        }

        // ── Input ──────────────────────────────────────────────────────────
        _root_ide_package_.com.trishit.egloo.ui.screens.ChatInputBar(
            text = state.inputText,
            onTextChange = com.trishit.egloo.domain.viewmodels.ChatViewModel::onInputChanged,
            onSend = com.trishit.egloo.domain.viewmodels.ChatViewModel::sendMessage,
            isSending = state.isSending,
        )
    }
}

@Composable
private fun ChatHeader(onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        _root_ide_package_.com.trishit.egloo.ui.components.PingoAvatar(size = 40.dp)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Ask Pingo", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Searches across all your sources",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onClear) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Clear chat",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun MessageBubble(message: com.trishit.egloo.domain.models.ChatMessage) {
    when (message) {
        is com.trishit.egloo.domain.models.ChatMessage.User -> _root_ide_package_.com.trishit.egloo.ui.screens.UserBubble(
            message
        )
        is com.trishit.egloo.domain.models.ChatMessage.Pingo -> _root_ide_package_.com.trishit.egloo.ui.screens.PingoBubble(
            message
        )
    }
}

@Composable
private fun UserBubble(message: com.trishit.egloo.domain.models.ChatMessage.User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(18.dp, 4.dp, 18.dp, 18.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun PingoBubble(message: com.trishit.egloo.domain.models.ChatMessage.Pingo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        _root_ide_package_.com.trishit.egloo.ui.components.PingoAvatar(size = 30.dp)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.widthIn(max = 300.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                if (message.isStreaming) {
                    _root_ide_package_.com.trishit.egloo.ui.screens.ThinkingDots()
                } else {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            // Source chips
            if (message.sources.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    message.sources.forEach { source ->
                        _root_ide_package_.com.trishit.egloo.ui.components.SourceBadge(source.type)
                    }
                }
            }
        }
    }
}

@Composable
private fun ThinkingDots() {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { i ->
            val anim by _root_ide_package_.com.trishit.egloo.ui.screens.animateFloatAsState(
                targetValue = 1f,
                label = "dot$i"
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
private fun ChatEmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        _root_ide_package_.com.trishit.egloo.ui.components.PingoAvatar(size = 64.dp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Ask me anything",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "I'll search across your Gmail, Slack, and Drive to find the answer.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))

        // Suggestion chips
        val suggestions = listOf(
            "What happened with Project Alpha?",
            "When is the Acme demo?",
            "What did the team decide about the budget?",
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            suggestions.forEach { suggestion ->
                // These are rendered as tappable suggestion pills
                // (onClick wiring handled in ChatInputBar calling viewModel)
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(suggestion, style = MaterialTheme.typography.bodySmall)
                    }
                )
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    isSending: Boolean,
) {
    Surface(
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Ask Pingo something…",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                },
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
            )
            FilledIconButton(
                onClick = onSend,
                enabled = text.isNotBlank() && !isSending,
            ) {
                if (isSending) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

// Unused import workaround for animateFloatAsState
private fun animateFloatAsState(targetValue: Float, label: String) =
    Animatable(targetValue)
