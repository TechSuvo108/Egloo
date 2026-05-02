package com.trishit.egloo.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

// ─────────────────────────────────────────────────────────────────────────────
// Destinations
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
sealed interface Destination {
    @Serializable data object Onboarding : com.trishit.egloo.navigation.Destination
    @Serializable data object Home       : com.trishit.egloo.navigation.Destination
    @Serializable data object Chat       : com.trishit.egloo.navigation.Destination
    @Serializable data object Topics     : com.trishit.egloo.navigation.Destination
    @Serializable data object Sources    : com.trishit.egloo.navigation.Destination
    @Serializable data object Settings   : com.trishit.egloo.navigation.Destination
}

// ─────────────────────────────────────────────────────────────────────────────
// Root component
// ─────────────────────────────────────────────────────────────────────────────

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    fun navigateTo(destination: com.trishit.egloo.navigation.Destination)
    fun onBackPressed()

    sealed class Child {
        class OnboardingChild(val component: ComponentContext) : Child()
        class HomeChild(val component: ComponentContext)       : Child()
        class ChatChild(val component: ComponentContext)       : Child()
        class TopicsChild(val component: ComponentContext)     : Child()
        class SourcesChild(val component: ComponentContext)    : Child()
        class SettingsChild(val component: ComponentContext)   : Child()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Default implementation
// ─────────────────────────────────────────────────────────────────────────────

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val isFirstLaunch: Boolean = true,
) : com.trishit.egloo.navigation.RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<com.trishit.egloo.navigation.Destination>()

    private val startDestination: com.trishit.egloo.navigation.Destination =
        if (isFirstLaunch) _root_ide_package_.com.trishit.egloo.navigation.Destination.Onboarding else _root_ide_package_.com.trishit.egloo.navigation.Destination.Home

    override val stack: Value<ChildStack<*, com.trishit.egloo.navigation.RootComponent.Child>> =
        childStack(
            source         = navigation,
            serializer     = _root_ide_package_.com.trishit.egloo.navigation.Destination.serializer(),
            initialStack   = { listOf(startDestination) },
            handleBackButton = true,
            childFactory   = ::createChild,
        )

    private fun createChild(
        destination: com.trishit.egloo.navigation.Destination,
        componentContext: ComponentContext,
    ): com.trishit.egloo.navigation.RootComponent.Child = when (destination) {
        _root_ide_package_.com.trishit.egloo.navigation.Destination.Onboarding -> _root_ide_package_.com.trishit.egloo.navigation.RootComponent.Child.OnboardingChild(componentContext)
        _root_ide_package_.com.trishit.egloo.navigation.Destination.Home       -> _root_ide_package_.com.trishit.egloo.navigation.RootComponent.Child.HomeChild(componentContext)
        _root_ide_package_.com.trishit.egloo.navigation.Destination.Chat       -> _root_ide_package_.com.trishit.egloo.navigation.RootComponent.Child.ChatChild(componentContext)
        _root_ide_package_.com.trishit.egloo.navigation.Destination.Topics     -> _root_ide_package_.com.trishit.egloo.navigation.RootComponent.Child.TopicsChild(componentContext)
        _root_ide_package_.com.trishit.egloo.navigation.Destination.Sources    -> _root_ide_package_.com.trishit.egloo.navigation.RootComponent.Child.SourcesChild(componentContext)
        _root_ide_package_.com.trishit.egloo.navigation.Destination.Settings   -> _root_ide_package_.com.trishit.egloo.navigation.RootComponent.Child.SettingsChild(componentContext)
    }

    override fun navigateTo(destination: com.trishit.egloo.navigation.Destination) {
        navigation.bringToFront(destination)
    }

    override fun onBackPressed() {
        navigation.pop()
    }
}
