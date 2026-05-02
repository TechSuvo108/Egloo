package com.trishit.egloo.data.repositories

import com.trishit.egloo.domain.models.ChatMessage
import com.trishit.egloo.data.dummy.*
import com.trishit.egloo.domain.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.Clock

// ─────────────────────────────────────────────────────────────────────────────
// Dummy implementations
// Replace with KtorXRepository when the backend is ready.
// See: data/api/ApiGuidelines.kt for the migration plan.
// ─────────────────────────────────────────────────────────────────────────────

class DummyDigestRepository : com.trishit.egloo.data.repositories.DigestRepository {
    override fun getDailyDigest(): Flow<com.trishit.egloo.data.repositories.DigestResult> = flow {
        emit(_root_ide_package_.com.trishit.egloo.data.repositories.DigestResult.Loading)
        delay(900) // simulate network
        emit(_root_ide_package_.com.trishit.egloo.data.repositories.DigestResult.Success(dummyDigest))
    }
}

class DummyChatRepository : com.trishit.egloo.data.repositories.ChatRepository {
    private val _messages = MutableStateFlow<List<com.trishit.egloo.domain.models.ChatMessage>>(emptyList())

    override fun getChatHistory(): Flow<List<com.trishit.egloo.domain.models.ChatMessage>> = _messages.asStateFlow()

    override suspend fun sendMessage(text: String) {
        val userMsg = _root_ide_package_.com.trishit.egloo.domain.models.ChatMessage.User(
            id = "u_${Clock.System.now().toEpochMilliseconds()}",
            text = text,
            sentAt = Clock.System.now(),
        )
        _messages.value = _messages.value + userMsg

        // Simulate Pingo thinking
        val thinkingMsg = ChatMessage.Pingo(
            id = "p_${Clock.System.now().toEpochMilliseconds()}",
            text = "",
            isStreaming = true,
            sentAt = Clock.System.now(),
        )
        _messages.value = _messages.value + thinkingMsg
        delay(1200)

        // Replace thinking with answer
        val answer = generateDummyAnswer(text)
        _messages.value = _messages.value.dropLast(1) + answer
    }

    override suspend fun clearHistory() {
        _messages.value = emptyList()
    }

    private fun generateDummyAnswer(question: String): com.trishit.egloo.domain.models.ChatMessage.Pingo {
        val lower = question.lowercase()
        val (text, sources) = when {
            "migration" in lower || "database" in lower ->
                Pair(
                    "The Postgres migration was postponed to Q3. Ali will lead the schema design sprint starting May 12. Documentation of the current schema needs to happen first.",
                    listOf(ChatSource("#project-alpha", _root_ide_package_.com.trishit.egloo.domain.models.SourceType.SLACK), ChatSource("Q2 Roadmap.gdoc", _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GOOGLE_DRIVE))
                )
            "acme" in lower || "demo" in lower ->
                Pair(
                    "The Acme Corp demo is on May 9 at 2 PM. Rachel confirmed via email — they want to see the new onboarding flow.",
                    listOf(ChatSource("rachel@acmecorp.com", _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL))
                )
            "budget" in lower || "infra" in lower ->
                Pair(
                    "\$24k was approved for the AWS infrastructure upgrade. The budget needs to be spent before June 30.",
                    listOf(ChatSource("finance@company.com", _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GMAIL))
                )
            "roadmap" in lower || "q2" in lower ->
                Pair(
                    "Q2 features are locked: Search v2, Analytics dashboard, and Team invites. AI features were moved to Q3. The final doc is in Drive.",
                    listOf(ChatSource("Product Roadmap.gdoc", _root_ide_package_.com.trishit.egloo.domain.models.SourceType.GOOGLE_DRIVE))
                )
            else ->
                Pair(
                    "I found a few relevant items in your knowledge base. Let me search more specifically — try asking about Project Alpha, the Acme demo, or the infra budget.",
                    emptyList()
                )
        }
        return _root_ide_package_.com.trishit.egloo.domain.models.ChatMessage.Pingo(
            id = "p_${Clock.System.now().toEpochMilliseconds()}",
            text = text,
            sources = sources,
            isStreaming = false,
            sentAt = Clock.System.now(),
        )
    }
}

class DummyTopicsRepository : com.trishit.egloo.data.repositories.TopicsRepository {
    override fun getTopics(): Flow<List<com.trishit.egloo.domain.models.Topic>> = flow {
        delay(400)
        emit(dummyTopics)
    }

    override fun getTopicById(id: String): Flow<com.trishit.egloo.domain.models.Topic?> = flow {
        emit(dummyTopics.find { it.id == id })
    }
}

class DummySourcesRepository : com.trishit.egloo.data.repositories.SourcesRepository {
    private val _sources = MutableStateFlow(dummySources)

    override fun getConnectedSources(): Flow<List<ConnectedSource>> = _sources.asStateFlow()

    override suspend fun connectSource(type: com.trishit.egloo.domain.models.SourceType) {
        delay(1500) // simulate OAuth
        _sources.value = _sources.value.map { source ->
            if (source.type == type) source.copy(isConnected = true, lastSyncedAt = Clock.System.now(), itemCount = 42)
            else source
        }
    }

    override suspend fun disconnectSource(id: String) {
        _sources.value = _sources.value.map { source ->
            if (source.id == id) source.copy(isConnected = false, lastSyncedAt = null, itemCount = 0)
            else source
        }
    }
}

class DummySettingsRepository : com.trishit.egloo.data.repositories.SettingsRepository {
    private val _settings = MutableStateFlow(dummySettings)

    override fun getSettings(): Flow<AppSettings> = _settings.asStateFlow()

    override suspend fun updateSettings(settings: AppSettings) {
        _settings.value = settings
    }
}
