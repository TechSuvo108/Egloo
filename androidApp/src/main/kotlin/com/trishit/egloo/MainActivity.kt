package com.trishit.egloo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.defaultComponentContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import com.trishit.egloo.navigation.DefaultRootComponent
import com.trishit.egloo.navigation.RootContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val root = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            isFirstLaunch = isFirstLaunch(),
        )
        setContent {
            RootContent(component = root)
        }
    }
    private fun isFirstLaunch(): Boolean {
        val prefs = getSharedPreferences("egloo_prefs", MODE_PRIVATE)
        return if (prefs.getBoolean("onboarding_done", false)) {
            false
        } else {
            prefs.edit { putBoolean("onboarding_done", true) }
            true
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    RootContent(
        component = DefaultRootComponent(
            componentContext = com.arkivanov.decompose.DefaultComponentContext(com.arkivanov.essenty.lifecycle.LifecycleRegistry()),
            isFirstLaunch = false
        )
    )
}