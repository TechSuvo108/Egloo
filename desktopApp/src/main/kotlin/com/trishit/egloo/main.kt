package com.trishit.egloo

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.mayakapps.compose.windowstyler.WindowBackdrop
import com.mayakapps.compose.windowstyler.WindowCornerPreference
import com.mayakapps.compose.windowstyler.WindowFrameStyle
import com.mayakapps.compose.windowstyler.WindowStyle
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.trishit.egloo.di.eglooModule
import com.trishit.egloo.navigation.DefaultRootComponent
import com.trishit.egloo.navigation.DesktopRootContent
import org.koin.core.context.startKoin
import java.util.prefs.Preferences

fun main() = application {
    // ── DI ────────────────────────────────────────────────────────────────────
    startKoin { modules(eglooModule) }

    // ── Decompose ─────────────────────────────────────────────────────────────
    val lifecycle = LifecycleRegistry()
    val root = remember {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle),
            isFirstLaunch = isFirstLaunch(),
        )
    }

    // ── Window state ──────────────────────────────────────────────────────────
    val windowState = rememberWindowState(
        width  = 1200.dp,
        height = 800.dp,
    )

    Window(
        onCloseRequest = ::exitApplication,
        state          = windowState,
        title          = "Egloo",
        // transparent = true is REQUIRED for Mica/Acrylic to show through.
        // Without this the JVM window paints an opaque background before Compose
        // draws, and the OS backdrop effect is completely hidden.
        transparent    = true,
        // undecorated = false keeps the native Win32 title bar (gives you
        // Snap Layouts on Win11 for free). On macOS the title bar is handled
        // natively regardless of this flag.
        undecorated    = false,
    ) {
        // ── ComposeWindowStyler ───────────────────────────────────────────────
        //
        // WindowStyle MUST be called inside the Window { } lambda.
        // It reads LocalWindow.current to get the HWND/NSWindow handle.
        //
        // Windows 11 (21H2+): WindowBackdrop.Mica — wallpaper-tinted dark sheet.
        //   Falls back automatically: Mica → Acrylic → Transparent (solid dark).
        // Windows 11 (22H2+): WindowBackdrop.Tabbed — same as Mica but for
        //   tabbed-style windows; upgrade if your app uses tabs.
        // Windows 10 / Linux: library falls back to Transparent with a dark solid
        //   color overlay, so the app still looks correct everywhere.
        // macOS: ComposeWindowStyler has no macOS backend. The window renders with
        //   the standard Compose opaque background. For true vibrancy on macOS you
        //   need a native NSVisualEffectView wrapper — out of scope for this prototype.
        //
        WindowStyle(
            isDarkTheme  = true,
            backdropType = WindowBackdrop.Mica,
            frameStyle   = WindowFrameStyle(
                cornerPreference = WindowCornerPreference.ROUNDED,  // Win11 rounded corners
            ),
        )

        // Root content — MUST use Color.Transparent as its background so the
        // Mica/Acrylic layer shows through. See DesktopRootContent in RootContent.kt.
        DesktopRootContent(component = root, darkTheme = true)
    }
}

private fun isFirstLaunch(): Boolean {
    val prefs = Preferences.userRoot().node("com/egloo")
    return if (prefs.getBoolean("onboarding_done", false)) {
        false
    } else {
        prefs.putBoolean("onboarding_done", true)
        true
    }
}
