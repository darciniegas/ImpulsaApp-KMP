package com.impulsa.app.navigation

import androidx.compose.runtime.Composable

/**
 * Intercepta el botón/gesto "atrás" del sistema.
 * Android: usa androidx.activity BackHandler. Desktop: no hace nada.
 */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
