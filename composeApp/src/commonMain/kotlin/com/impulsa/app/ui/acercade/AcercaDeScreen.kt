package com.impulsa.app.ui.acercade

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.impulsa.app.core.AppInfo
import com.impulsa.app.core.Textos
import com.impulsa.app.resources.Res
import com.impulsa.app.resources.impulsa
import com.impulsa.app.ui.components.ContenidoCentrado
import com.impulsa.app.ui.components.Etiqueta
import com.impulsa.app.ui.components.ImpulsaScaffold
import com.impulsa.app.ui.components.TarjetaImpulsa
import org.jetbrains.compose.resources.painterResource

/** PANTALLA 9 — Acerca de Impulsa: descripción, tecnologías, plataformas y créditos. */
@Composable
fun AcercaDeScreen(alVolver: () -> Unit) {
    val colores = MaterialTheme.colorScheme

    ImpulsaScaffold(titulo = Textos.ACERCA_DE, alVolver = alVolver) { padding ->
        ContenidoCentrado(modifier = Modifier.padding(padding), anchoMaximo = 640.dp) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(shape = RoundedCornerShape(28.dp), color = Color.White, shadowElevation = 2.dp) {
                    Image(
                        painter = painterResource(Res.drawable.impulsa),
                        contentDescription = Textos.DESC_LOGO,
                        modifier = Modifier.size(180.dp)
                    )
                }
                Text(
                    text = Textos.APP_NOMBRE,
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colores.onSurface
                )
                Text(
                    text = "${AppInfo.LEMA}  ·  v${AppInfo.VERSION}",
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colores.onSurfaceVariant
                )

                Seccion("Descripción") { Parrafo(AppInfo.DESCRIPCION) }
                Seccion("Objetivo") { Parrafo(AppInfo.OBJETIVO) }
                Seccion("Tecnologías utilizadas") { AppInfo.TECNOLOGIAS.forEach { Parrafo("• $it") } }
                Seccion("Plataformas implementadas") { AppInfo.PLATAFORMAS.forEach { Parrafo("• $it") } }
                Seccion("Desarrolladores") { AppInfo.INTEGRANTES.forEach { Parrafo("• $it") } }
                Seccion("Información académica") {
                    Parrafo(AppInfo.ASIGNATURA)
                    Parrafo(AppInfo.PROYECTO)
                    Parrafo(AppInfo.INSTITUCION)
                    Parrafo(AppInfo.PERIODO)
                }
            }
        }
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    TarjetaImpulsa(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
            Etiqueta(titulo, color = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.padding(top = 6.dp)) { contenido() }
        }
    }
}

@Composable
private fun Parrafo(texto: String) {
    Text(
        text = texto,
        modifier = Modifier.padding(vertical = 2.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}
