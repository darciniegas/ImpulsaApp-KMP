package com.impulsa.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Colores de marca muestreados del LOGO OFICIAL de Impulsa (impulsa.jpg).
 * La paleta Material 3 de abajo se deriva de ellos (violeta = primario, magenta = secundario,
 * azul = terciario, índigo = texto sobre contenedores).
 */
object ImpulsaBrand {
    val Magenta = Color(0xFFF41AE3)   // base del isotipo
    val Rosa = Color(0xFFFB1C9D)      // punto del wordmark / "AVANZA"
    val Violeta = Color(0xFF8921FC)   // curva central / "CRECE"
    val Azul = Color(0xFF03B0FD)      // flecha
    val Cian = Color(0xFF02D3FC)      // punta de la flecha
    val Indigo = Color(0xFF280C83)    // texto "impulsa"

    /** Degradado del encabezado del Dashboard: magenta → violeta → azul (como el isotipo). */
    val GradienteEncabezado = listOf(Color(0xFFC2187F), Color(0xFF7A2FE6), Color(0xFF0B6BD3))
}

val LightColors = lightColorScheme(
    primary = Color(0xFF7A2FE6),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF280C83),
    secondary = Color(0xFFC2187F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD8EC),
    onSecondaryContainer = Color(0xFF3D0024),
    tertiary = Color(0xFF0B6BD3),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD3E8FF),
    onTertiaryContainer = Color(0xFF001C38),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFCF8FF),
    onBackground = Color(0xFF1C1B22),
    surface = Color(0xFFFCF8FF),
    onSurface = Color(0xFF1C1B22),
    surfaceVariant = Color(0xFFE6DFF0),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF7A7586),
    outlineVariant = Color(0xFFCAC4D4),
    inverseSurface = Color(0xFF312F37),
    inverseOnSurface = Color(0xFFF3EFF8),
    inversePrimary = Color(0xFFD3BBFF),
    surfaceTint = Color(0xFF7A2FE6),
    scrim = Color(0xFF000000),
    surfaceDim = Color(0xFFDCD8E0),
    surfaceBright = Color(0xFFFCF8FF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF6F2FB),
    surfaceContainer = Color(0xFFF0ECF5),
    surfaceContainerHigh = Color(0xFFEAE6EF),
    surfaceContainerHighest = Color(0xFFE5E1E9)
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFD3BBFF),
    onPrimary = Color(0xFF3F1590),
    primaryContainer = Color(0xFF5522B8),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFFFADD6),
    onSecondary = Color(0xFF5F0039),
    secondaryContainer = Color(0xFF8A0F5B),
    onSecondaryContainer = Color(0xFFFFD8EC),
    tertiary = Color(0xFFA5C8FF),
    onTertiary = Color(0xFF00315F),
    tertiaryContainer = Color(0xFF00488A),
    onTertiaryContainer = Color(0xFFD3E8FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E1EA),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFE6E1EA),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D4),
    outline = Color(0xFF948F9E),
    outlineVariant = Color(0xFF49454F),
    inverseSurface = Color(0xFFE6E1EA),
    inverseOnSurface = Color(0xFF312F37),
    inversePrimary = Color(0xFF7A2FE6),
    surfaceTint = Color(0xFFD3BBFF),
    scrim = Color(0xFF000000),
    surfaceDim = Color(0xFF141218),
    surfaceBright = Color(0xFF3B383E),
    surfaceContainerLowest = Color(0xFF0F0D13),
    surfaceContainerLow = Color(0xFF1C1B20),
    surfaceContainer = Color(0xFF201F25),
    surfaceContainerHigh = Color(0xFF2B292F),
    surfaceContainerHighest = Color(0xFF36343A)
)
