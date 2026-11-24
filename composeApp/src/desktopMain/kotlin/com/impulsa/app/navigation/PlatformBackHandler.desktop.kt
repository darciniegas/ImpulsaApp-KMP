package com.impulsa.app.navigation

import androidx.compose.runtime.Composable

/** En escritorio no existe el botón "atrás" del sistema: la navegación inversa se hace con la flecha de cada pantalla. */
@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Intencionalmente vacío.
}
