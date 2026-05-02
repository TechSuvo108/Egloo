package com.trishit.egloo.di

import com.trishit.egloo.domain.viewmodels.ChatViewModel
import com.trishit.egloo.domain.viewmodels.HomeViewModel
import com.trishit.egloo.domain.viewmodels.SettingsViewModel
import com.trishit.egloo.domain.viewmodels.SourcesViewModel
import com.trishit.egloo.domain.viewmodels.TopicsViewModel
import com.trishit.egloo.data.repositories.ChatRepository
import com.trishit.egloo.data.repositories.DigestRepository
import com.trishit.egloo.data.repositories.DummyChatRepository
import com.trishit.egloo.data.repositories.DummyDigestRepository
import com.trishit.egloo.data.repositories.DummySettingsRepository
import com.trishit.egloo.data.repositories.DummySourcesRepository
import com.trishit.egloo.data.repositories.DummyTopicsRepository
import com.trishit.egloo.data.repositories.SettingsRepository
import com.trishit.egloo.data.repositories.SourcesRepository
import com.trishit.egloo.data.repositories.TopicsRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

// ─────────────────────────────────────────────────────────────────────────────
// EglooModule
//
// To migrate to real backend:
//   Replace "DummyXRepository()" with "KtorXRepository(get())" and add:
//   single { createHttpClient(BASE_URL) { get<TokenStore>().getToken() } }
// ─────────────────────────────────────────────────────────────────────────────

val eglooModule = module {

    // ── Repositories ──────────────────────────────────────────────────────────
    singleOf(::DummyDigestRepository)  bind _root_ide_package_.com.trishit.egloo.data.repositories.DigestRepository::class
    singleOf(::DummyChatRepository)    bind _root_ide_package_.com.trishit.egloo.data.repositories.ChatRepository::class
    singleOf(::DummyTopicsRepository)  bind _root_ide_package_.com.trishit.egloo.data.repositories.TopicsRepository::class
    singleOf(::DummySourcesRepository) bind _root_ide_package_.com.trishit.egloo.data.repositories.SourcesRepository::class
    singleOf(::DummySettingsRepository) bind _root_ide_package_.com.trishit.egloo.data.repositories.SettingsRepository::class

    // ── ViewModels ────────────────────────────────────────────────────────────
    factoryOf(::HomeViewModel)
    factoryOf(::ChatViewModel)
    factoryOf(::TopicsViewModel)
    factoryOf(::SourcesViewModel)
    factoryOf(::SettingsViewModel)
}
