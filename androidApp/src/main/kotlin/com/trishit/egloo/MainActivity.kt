package com.trishit.egloo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import com.trishit.egloo.di.eglooModule
import com.trishit.egloo.navigation.DefaultRootComponent
import com.trishit.egloo.navigation.RootContent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(applicationContext)
            modules(eglooModule)
        }

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
    App()
}