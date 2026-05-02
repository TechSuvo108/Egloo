package com.trishit.egloo.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.trishit.egloo.domain.models.*
import com.trishit.egloo.ui.theme.EglooColors
import com.trishit.egloo.domain.models.SourceType
import com.trishit.egloo.ui.components.timeAgo
import kotlin.time.Clock
import kotlin.time.Instant


// ── Source badge ──────────────────────────────────────────────────────────────

@Composable
fun SourceBadge(type: com.trishit.egloo.domain.models.SourceType, modifier: Modifier = Modifier) {
    val (bg, fg, label) = when (type) {
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL        -> Triple(Color(0x22EA4335), _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.GmailRed,    "Gmail")
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK        -> Triple(Color(0x22611f69), _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.SlackPurple, "Slack")
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GOOGLE_DRIVE -> Triple(Color(0x221967D2), _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.DriveBlue,   "Drive")
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.NOTION       -> Triple(Color(0x2237352F), _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.NotionGray,  "Notion")
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.PDF          -> Triple(Color(0x22FF5722), _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.PdfOrange,   "PDF")
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.MANUAL       -> Triple(Color(0x22888888), Color(0xFF888888),       "Manual")
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
        )
    }
}

// ── Source dot (compact) ──────────────────────────────────────────────────────

@Composable
fun SourceDot(type: com.trishit.egloo.domain.models.SourceType, modifier: Modifier = Modifier) {
    val color = when (type) {
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL        -> _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.GmailRed
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK        -> _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.SlackPurple
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GOOGLE_DRIVE -> _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.DriveBlue
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.NOTION       -> _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.NotionGray
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.PDF          -> _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.PdfOrange
        _root_ide_package_.com.trishit.egloo.domain.models.SourceType.MANUAL       -> Color(0xFF888888)
    }
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

// ── Knowledge item card ───────────────────────────────────────────────────────

@Composable
fun KnowledgeCard(
    item: KnowledgeItem,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Source + time row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                _root_ide_package_.com.trishit.egloo.ui.components.SourceDot(item.sourceType)
                Text(
                    text = item.sourceLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = item.createdAt.timeAgo(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }

            Spacer(Modifier.height(6.dp))

            // Title
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(4.dp))

            // Summary
            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            // Project tag
            if (item.projectTag != null) {
                Spacer(Modifier.height(8.dp))
                _root_ide_package_.com.trishit.egloo.ui.components.ProjectTag(item.projectTag)
            }
        }
    }
}

// ── Project tag pill ──────────────────────────────────────────────────────────

@Composable
fun ProjectTag(tag: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

// ── Pingo avatar (used in chat + onboarding) ──────────────────────────────────

@Composable
fun PingoAvatar(size: Dp = 36.dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(_root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.TealDarker),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "P",
            style = MaterialTheme.typography.titleMedium,
            color = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.TealLighter,
        )
    }
}

// ── Action item row ────────────────────────────────────────────────────────────

@Composable
fun ActionItemRow(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(_root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.BeakAmber)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ── Pingo message bubble (onboarding / tips) ──────────────────────────────────

@Composable
fun PingoMessageBubble(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(_root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.TealDarker.copy(alpha = 0.4f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        _root_ide_package_.com.trishit.egloo.ui.components.PingoAvatar(size = 32.dp)
        Column {
            Text(
                text = "Pingo",
                style = MaterialTheme.typography.labelMedium,
                color = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.TealLighter,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

fun Instant.timeAgo(): String {
    val now = Clock.System.now()
    val diff = now - this
    return when {
        diff.inWholeMinutes < 1  -> "just now"
        diff.inWholeMinutes < 60 -> "${diff.inWholeMinutes}m ago"
        diff.inWholeHours < 24   -> "${diff.inWholeHours}h ago"
        else                     -> "${diff.inWholeDays}d ago"
    }
}
