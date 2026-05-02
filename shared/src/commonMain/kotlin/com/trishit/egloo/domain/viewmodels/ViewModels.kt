package com.trishit.egloo.domain.viewmodels

import com.trishit.egloo.data.repositories.*
import com.trishit.egloo.domain.models.*
import com.trishit.egloo.data.repositories.ChatRepository
import com.trishit.egloo.data.repositories.DigestRepository
import com.trishit.egloo.data.repositories.DigestResult
import com.trishit.egloo.data.repositories.SettingsRepository
import com.trishit.egloo.data.repositories.SourcesRepository
import com.trishit.egloo.data.repositories.TopicsRepository
import com.trishit.egloo.domain.models.ChatMessage
import com.trishit.egloo.domain.models.SourceType
import com.trishit.egloo.domain.models.Topic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// ── Base ──────────────────────────────────────────────────────────────────────

abstract class BaseViewModel {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    fun dispose() = scope.cancel()
}

// ── Home / Digest ─────────────────────────────────────────────────────────────

data class HomeUiState(
    val isLoading: Boolean = true,
    val digest: DailyDigest? = null,
    val error: String? = null,
)

class HomeViewModel(private val digestRepo: com.trishit.egloo.data.repositories.DigestRepository) : com.trishit.egloo.domain.viewmodels.BaseViewModel() {

    private val _uiState = MutableStateFlow(_root_ide_package_.com.trishit.egloo.domain.viewmodels.HomeUiState())
    val uiState: StateFlow<com.trishit.egloo.domain.viewmodels.HomeUiState> = _uiState.asStateFlow()

    init { loadDigest() }

    fun loadDigest() {
        _root_ide_package_.com.trishit.egloo.domain.viewmodels.BaseViewModel.scope.launch {
            digestRepo.getDailyDigest().collect { result ->
                _uiState.value = when (result) {
                    is com.trishit.egloo.data.repositories.DigestResult.Loading -> _root_ide_package_.com.trishit.egloo.domain.viewmodels.HomeUiState(
                        isLoading = true
                    )
                    is com.trishit.egloo.data.repositories.DigestResult.Success -> _root_ide_package_.com.trishit.egloo.domain.viewmodels.HomeUiState(
                        isLoading = false,
                        digest = result.digest
                    )
                    is com.trishit.egloo.data.repositories.DigestResult.Error   -> _root_ide_package_.com.trishit.egloo.domain.viewmodels.HomeUiState(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}

// ── Chat ──────────────────────────────────────────────────────────────────────

data class ChatUiState(
    val messages: List<com.trishit.egloo.domain.models.ChatMessage> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
)

class ChatViewModel(private val chatRepo: com.trishit.egloo.data.repositories.ChatRepository) : com.trishit.egloo.domain.viewmodels.BaseViewModel() {

    private val _uiState = MutableStateFlow(_root_ide_package_.com.trishit.egloo.domain.viewmodels.ChatUiState())
    val uiState: StateFlow<com.trishit.egloo.domain.viewmodels.ChatUiState> = _uiState.asStateFlow()

    init {
        _root_ide_package_.com.trishit.egloo.domain.viewmodels.BaseViewModel.scope.launch {
            chatRepo.getChatHistory().collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return
        _uiState.update { it.copy(inputText = "", isSending = true) }
        _root_ide_package_.com.trishit.egloo.domain.viewmodels.BaseViewModel.scope.launch {
            chatRepo.sendMessage(text)
            _uiState.update { it.copy(isSending = false) }
        }
    }

    fun clearChat() {
        _root_ide_package_.com.trishit.egloo.domain.viewmodels.BaseViewModel.scope.launch { chatRepo.clearHistory() }
    }
}

// ── Topics ────────────────────────────────────────────────────────────────────

data class TopicsUiState(
    val isLoading: Boolean = true,
    val topics: List<com.trishit.egloo.domain.models.Topic> = emptyList(),
    val selectedTopic: com.trishit.egloo.domain.models.Topic? = null,
)

class TopicsViewModel(private val topicsRepo: com.trishit.egloo.data.repositories.TopicsRepository) : com.trishit.egloo.domain.viewmodels.BaseViewModel() {

    private val _uiState = MutableStateFlow(_root_ide_package_.com.trishit.egloo.domain.viewmodels.TopicsUiState())
    val uiState: StateFlow<com.trishit.egloo.domain.viewmodels.TopicsUiState> = _uiState.asStateFlow()

    init {
        _root_ide_package_.com.trishit.egloo.domain.viewmodels.BaseViewModel.scope.launch {
            topicsRepo.getTopics().collect { topics ->
                _uiState.update { it.copy(isLoading = false, topics = topics) }
            }
        }
    }

    fun selectTopic(topic: com.trishit.egloo.domain.models.Topic?) {
        _uiState.update { it.copy(selectedTopic = topic) }
    }
}

// ── Sources ───────────────────────────────────────────────────────────────────

data class SourcesUiState(
    val sources: List<ConnectedSource> = emptyList(),
    val connectingType: com.trishit.egloo.domain.models.SourceType? = null,
)

class SourcesViewModel(private val sourcesRepo: com.trishit.egloo.data.repositories.SourcesRepository) : com.trishit.egloo.domain.viewmodels.BaseViewModel() {

    private val _uiState = MutableStateFlow(_root_ide_package_.com.trishit.egloo.domain.viewmodels.SourcesUiState())
    val uiState: StateFlow<com.trishit.egloo.domain.viewmodels.SourcesUiState> = _uiState.asStateFlow()

    init {
        _root_ide_package_.com.trishit.egloo.domain.viewmodels.BaseViewModel.scope.launch {
            sourcesRepo.getConnectedSources().collect { sources ->
                _uiState.update { it.copy(sources = sources) }
            }
        }
    }

    fun connectSource(type: com.trishit.egloo.domain.models.SourceType) {
        _uiState.update { it.copy(connectingType = type) }
        _root_ide_package_.com.trishit.egloo.domain.viewmodels.BaseViewModel.scope.launch {
            sourcesRepo.connectSource(type)
            _uiState.update { it.copy(connectingType = null) }
        }
    }

    fun disconnectSource(id: String) {
        _root_ide_package_.com.trishit.egloo.domain.viewmodels.BaseViewModel.scope.launch { sourcesRepo.disconnectSource(id) }
    }
}

// ── Settings ──────────────────────────────────────────────────────────────────

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val isSaved: Boolean = false,
)

class SettingsViewModel(private val settingsRepo: com.trishit.egloo.data.repositories.SettingsRepository) : com.trishit.egloo.domain.viewmodels.BaseViewModel() {

    private val _uiState = MutableStateFlow(_root_ide_package_.com.trishit.egloo.domain.viewmodels.SettingsUiState())
    val uiState: StateFlow<com.trishit.egloo.domain.viewmodels.SettingsUiState> = _uiState.asStateFlow()

    init {
        _root_ide_package_.com.trishit.egloo.domain.viewmodels.BaseViewModel.scope.launch {
            settingsRepo.getSettings().collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    fun updateUserName(name: String) = update { it.copy(userName = name) }
    fun toggleDarkTheme(enabled: Boolean) = update { it.copy(darkTheme = enabled) }
    fun togglePingoGreetings(enabled: Boolean) = update { it.copy(pingoGreetingsEnabled = enabled) }
    fun toggleDigestNotifications(enabled: Boolean) = update { it.copy(digestNotificationsEnabled = enabled) }
    fun setSyncFrequency(hours: Int) = update { it.copy(syncFrequencyHours = hours) }

    private fun update(block: (AppSettings) -> AppSettings) {
        _root_ide_package_.com.trishit.egloo.domain.viewmodels.BaseViewModel.scope.launch {
            val updated = block(_uiState.value.settings)
            settingsRepo.updateSettings(updated)
            _uiState.update { it.copy(isSaved = true) }
            delay(1500)
            _uiState.update { it.copy(isSaved = false) }
        }
    }
}
