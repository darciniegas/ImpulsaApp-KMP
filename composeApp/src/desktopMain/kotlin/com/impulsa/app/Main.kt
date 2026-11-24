package com.impulsa.app

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.impulsa.app.data.DatabaseDriverFactory
import com.impulsa.app.resources.Res
import com.impulsa.app.resources.impulsa_icon
import org.jetbrains.compose.resources.painterResource
import java.awt.Dimension

/** Punto de entrada de Desktop (Windows): abre una ventana con la misma UI compartida. */
fun main() = application {
    val estadoVentana = rememberWindowState(width = 1100.dp, height = 780.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Impulsa",
        state = estadoVentana,
        icon = painterResource(Res.drawable.impulsa_icon)
    ) {
        window.minimumSize = Dimension(420, 640)
        ImpulsaApp(driverFactory = DatabaseDriverFactory())
    }
}
