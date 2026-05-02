package com.trishit.egloo.domain.models

import kotlinx.serialization.Serializable

// ─────────────────────────────────────────────
//  Core domain models
//  These are the canonical data shapes used
//  across all screens. The API layer will map
//  DTOs → these models (see API guidelines).
// ─────────────────────────────────────────────

enum class SourceType { GMAIL, SLACK, DRIVE, NOTION, PDF, GOOGLE_DRIVE, MANUAL }

@Serializable
data class Source(
    val id: String,
    val type: SourceType,
    val displayName: String,
    val isConnected: Boolean,
    val lastSyncedAt: String? = null,   // ISO-8601; null = never synced
    val itemCount: Int = 0
)

@Serializable
data class DigestItem(
    val id: String,
    val title: String,
    val summary: String,
    val sourceType: com.trishit.egloo.domain.models.SourceType,
    val sourceName: String,          // e.g. "Project Alpha · Slack"
    val timestamp: String,           // human-readable, e.g. "2h ago"
    val isActionItem: Boolean = false,
    val tags: List<String> = emptyList()
)

@Serializable
data class Topic(
    val id: String,
    val name: String,
    val summary: String,
    val itemCount: Int,
    val sources: List<com.trishit.egloo.domain.models.SourceType>,
    val lastActivityAt: String
)

@Serializable
data class ChatMessage(
    val id: String,
    val role: Role,
    val content: String,
    val sourceRefs: List<com.trishit.egloo.domain.models.SourceRef> = emptyList(),  // citations shown below AI messages
    val timestamp: String = ""
) {
    enum class Role { USER, PINGO }
}

@Serializable
data class SourceRef(
    val label: String,               // e.g. "Slack · #dev-general"
    val sourceType: com.trishit.egloo.domain.models.SourceType
)

@Serializable
data class SavedItem(
    val id: String,
    val title: String,
    val excerpt: String,
    val sourceType: com.trishit.egloo.domain.models.SourceType,
    val savedAt: String
)
