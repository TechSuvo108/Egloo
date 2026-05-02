package com.trishit.egloo.data.dummy

import com.trishit.egloo.domain.models.ChatMessage
import com.trishit.egloo.domain.models.DigestItem
import com.trishit.egloo.domain.models.SavedItem
import com.trishit.egloo.domain.models.Source
import com.trishit.egloo.domain.models.SourceRef
import com.trishit.egloo.domain.models.SourceType
import com.trishit.egloo.domain.models.Topic

// ─────────────────────────────────────────────
//  Dummy data — Phase 1–5 placeholder content
//  Replace repository return values with real
//  Ktor API calls in Phase 6.
// ─────────────────────────────────────────────

object DummyData {

    val sources = listOf(
        _root_ide_package_.com.trishit.egloo.domain.models.Source(
            "src_gmail",
            _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL,
            "Gmail",
            isConnected = true,
            lastSyncedAt = "2025-05-02T08:00:00Z",
            itemCount = 342
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.Source(
            "src_slack",
            _root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK,
            "Slack",
            isConnected = true,
            lastSyncedAt = "2025-05-02T09:15:00Z",
            itemCount = 1204
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.Source(
            "src_drive",
            _root_ide_package_.com.trishit.egloo.domain.models.SourceType.DRIVE,
            "Google Drive",
            isConnected = false,
            lastSyncedAt = null,
            itemCount = 0
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.Source(
            "src_notion",
            _root_ide_package_.com.trishit.egloo.domain.models.SourceType.NOTION,
            "Notion",
            isConnected = false,
            lastSyncedAt = null,
            itemCount = 0
        ),
    )

    val digestItems = listOf(
        _root_ide_package_.com.trishit.egloo.domain.models.DigestItem(
            id = "d1",
            title = "Database migration decision",
            summary = "Team agreed to defer migration to v2 schema until after June release. Sarah flagged 3 blocking issues in #backend.",
            sourceType = _root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK,
            sourceName = "Slack · #backend",
            timestamp = "2h ago",
            isActionItem = true,
            tags = listOf("Project Alpha", "Engineering")
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.DigestItem(
            id = "d2",
            title = "Client feedback on designs",
            summary = "Nexus Corp approved the dashboard mockups. They want a dark mode variant before the May 15 presentation.",
            sourceType = _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL,
            sourceName = "Gmail · Nexus Corp",
            timestamp = "4h ago",
            isActionItem = true,
            tags = listOf("Client", "Design")
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.DigestItem(
            id = "d3",
            title = "Q2 roadmap doc updated",
            summary = "Alex added the AI search feature to Q3 scope. Analytics dashboard moved to Q4.",
            sourceType = _root_ide_package_.com.trishit.egloo.domain.models.SourceType.DRIVE,
            sourceName = "Drive · Roadmap 2025",
            timestamp = "Yesterday",
            isActionItem = false,
            tags = listOf("Planning")
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.DigestItem(
            id = "d4",
            title = "Sprint retrospective notes",
            summary = "Velocity down 18% due to on-call incidents. Team voted to add 20% buffer to sprint capacity.",
            sourceType = _root_ide_package_.com.trishit.egloo.domain.models.SourceType.NOTION,
            sourceName = "Notion · Sprint Notes",
            timestamp = "Yesterday",
            isActionItem = false,
            tags = listOf("Engineering", "Process")
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.DigestItem(
            id = "d5",
            title = "Budget approval email",
            summary = "Finance approved the \\$12k infrastructure upgrade. Purchase order should be raised by May 10.",
            sourceType = _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL,
            sourceName = "Gmail · Finance",
            timestamp = "2 days ago",
            isActionItem = true,
            tags = listOf("Finance")
        ),
    )

    val topics = listOf(
        _root_ide_package_.com.trishit.egloo.domain.models.Topic(
            "t1",
            "Project Alpha",
            "Active sprint, 2 blockers. Client review on May 15.",
            14,
            listOf(_root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK,
                _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL,
                _root_ide_package_.com.trishit.egloo.domain.models.SourceType.DRIVE
            ),
            "2h ago"
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.Topic(
            "t2",
            "Database Migration",
            "Deferred to post-June release. 3 open issues.",
            7,
            listOf(_root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK,
                _root_ide_package_.com.trishit.egloo.domain.models.SourceType.NOTION
            ),
            "2h ago"
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.Topic(
            "t3",
            "Q2 Planning",
            "Roadmap updated. AI search moved to Q3.",
            11,
            listOf(_root_ide_package_.com.trishit.egloo.domain.models.SourceType.DRIVE,
                _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL
            ),
            "Yesterday"
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.Topic(
            "t4",
            "Client: Nexus Corp",
            "Designs approved. Dark mode variant requested.",
            5,
            listOf(_root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL),
            "4h ago"
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.Topic(
            "t5",
            "Infrastructure",
            "\\$12k budget approved. PO deadline May 10.",
            3,
            listOf(_root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL),
            "2 days ago"
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.Topic(
            "t6",
            "Team Processes",
            "Sprint velocity issue flagged. Buffer policy proposed.",
            4,
            listOf(_root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK,
                _root_ide_package_.com.trishit.egloo.domain.models.SourceType.NOTION
            ),
            "Yesterday"
        ),
    )

    val initialChatMessages = listOf(
        _root_ide_package_.com.trishit.egloo.domain.models.ChatMessage(
            id = "m0",
            role = _root_ide_package_.com.trishit.egloo.domain.models.ChatMessage.Role.PINGO,
            content = "Hey! I'm Pingo 🐧 I've read through your emails, Slack, and docs. Ask me anything about your work — I've got it all stored safely in the igloo.",
            timestamp = "Now"
        )
    )

    val pingoCannedResponses = mapOf(
        "database" to _root_ide_package_.com.trishit.egloo.domain.models.ChatMessage(
            id = "m_db",
            role = _root_ide_package_.com.trishit.egloo.domain.models.ChatMessage.Role.PINGO,
            content = "The team decided to defer the database migration to v2 schema until after the June release. Sarah flagged 3 blocking issues in #backend. The main concerns are downtime risk and the Auth service dependency.",
            sourceRefs = listOf(
                _root_ide_package_.com.trishit.egloo.domain.models.SourceRef("Slack · #backend · May 2", _root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK),
                _root_ide_package_.com.trishit.egloo.domain.models.SourceRef("Notion · Sprint Notes", _root_ide_package_.com.trishit.egloo.domain.models.SourceType.NOTION),
            ),
            timestamp = "Just now"
        ),
        "project alpha" to _root_ide_package_.com.trishit.egloo.domain.models.ChatMessage(
            id = "m_pa",
            role = _root_ide_package_.com.trishit.egloo.domain.models.ChatMessage.Role.PINGO,
            content = "Project Alpha is in active sprint. There are 2 blockers: the DB migration decision (deferred) and the dark mode variant Nexus Corp requested. The client review is scheduled for May 15.",
            sourceRefs = listOf(
                _root_ide_package_.com.trishit.egloo.domain.models.SourceRef("Slack · #project-alpha", _root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK),
                _root_ide_package_.com.trishit.egloo.domain.models.SourceRef("Gmail · Nexus Corp · May 2", _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL),
            ),
            timestamp = "Just now"
        ),
        "default" to _root_ide_package_.com.trishit.egloo.domain.models.ChatMessage(
            id = "m_default",
            role = _root_ide_package_.com.trishit.egloo.domain.models.ChatMessage.Role.PINGO,
            content = "I found a few things related to that across your sources. Here's what I know — let me know if you want me to dig deeper into any specific part.",
            sourceRefs = listOf(
                _root_ide_package_.com.trishit.egloo.domain.models.SourceRef("Slack · #general", _root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK),
            ),
            timestamp = "Just now"
        )
    )

    val savedItems = listOf(
        _root_ide_package_.com.trishit.egloo.domain.models.SavedItem(
            "s1",
            "Database migration decision",
            "Team agreed to defer migration...",
            _root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK,
            "Today"
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.SavedItem(
            "s2",
            "Q2 Roadmap v3",
            "AI search feature added to Q3 scope...",
            _root_ide_package_.com.trishit.egloo.domain.models.SourceType.DRIVE,
            "Yesterday"
        ),
        _root_ide_package_.com.trishit.egloo.domain.models.SavedItem(
            "s3",
            "Nexus Corp feedback email",
            "Approved dashboard mockups. Dark mode needed...",
            _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL,
            "Today"
        ),
    )
}
