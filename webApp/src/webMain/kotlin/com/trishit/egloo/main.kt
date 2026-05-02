package com.trishit.egloo

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin { modules(eglooModule) }
    val lifecycle = LifecycleRegistry()
    val root = DefaultRootComponent(
        componentContext = DefaultComponentContext(lifecycle),
        isFirstLaunch    = wasmIsFirstLaunch(),
    )
    ComposeViewport {
        RootContent(component = root, darkTheme = true)
    }
}
private fun wasmIsFirstLaunch(): Boolean {
    val key = "egloo_onboarding_done"
    return if (localStorage.getItem(key) != null) {
        false
    } else {
        localStorage.setItem(key, "true")
        true
    }
}