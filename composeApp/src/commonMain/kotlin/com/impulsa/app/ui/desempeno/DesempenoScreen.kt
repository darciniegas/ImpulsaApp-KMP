package com.impulsa.app.ui.desempeno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.impulsa.app.core.Textos
import com.impulsa.app.core.aMoneda
import com.impulsa.app.ui.components.AsesorItem
import com.impulsa.app.ui.components.BarraProgreso
import com.impulsa.app.ui.components.ContenidoCentrado
import com.impulsa.app.ui.components.ImpulsaScaffold
import com.impulsa.app.ui.components.ModoAsesor
import com.impulsa.app.ui.components.TarjetaImpulsa
import com.impulsa.app.ui.theme.ImpulsaIcons
import kotlinx.coroutines.launch

/** PANTALLA 4 — Desempeño: métricas del equipo y ranking de asesores. */
@Composable
fun DesempenoScreen(viewModel: DesempenoViewModel) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val usuario = estado.usuario

    ImpulsaScaffold(titulo = Textos.NAV_DESEMPENO, snackbarHostState = snackbarHostState) { padding ->
        ContenidoCentrado(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // ───── Resumen general ─────
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResumenTarjeta(
                        modifier = Modifier.weight(1f),
                        icono = ImpulsaIcons.Trofeo,
                        valor = estado.ingresosEquipo.aMoneda(),
                        etiqueta = "Ingresos del equipo",
                        fondo = MaterialTheme.colorScheme.primaryContainer,
                        contenido = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    ResumenTarjeta(
                        modifier = Modifier.weight(1f),
                        icono = ImpulsaIcons.Meta,
                        valor = "${estado.cumplimientoEquipo}%",
                        etiqueta = "Cumplimiento de meta",
                        fondo = MaterialTheme.colorScheme.tertiaryContainer,
                        contenido = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                // ───── Mi avance personal ─────
                TarjetaImpulsa(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Mi avance personal",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (usuario != null) "${usuario.ventas} ventas cerradas · ${usuario.ingresos.aMoneda()}" else "",
                            modifier = Modifier.padding(top = 2.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        BarraProgreso(
                            porcentaje = usuario?.porcentajeMeta ?: 0,
                            grosor = 8.dp,
                            modifier = Modifier.padding(top = 14.dp)
                        )
                    }
                }

                // ───── Ranking del equipo ─────
                Text(
                    text = "Ranking del equipo",
                    modifier = Modifier.padding(top = 16.dp, bottom = 10.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    estado.ranking.forEachIndexed { indice, asesor ->
                        AsesorItem(
                            asesor = asesor,
                            posicion = indice + 1,
                            modo = ModoAsesor.RANKING,
                            alPulsar = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("${asesor.nombre}: ${asesor.ventas} ventas")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenTarjeta(
    icono: ImageVector,
    valor: String,
    etiqueta: String,
    fondo: Color,
    contenido: Color,
    modifier: Modifier = Modifier
) {
    TarjetaImpulsa(modifier = modifier, colorFondo = fondo) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Icon(icono, contentDescription = null, tint = contenido, modifier = Modifier.size(28.dp))
            Text(
                text = valor,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contenido
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = contenido
            )
        }
    }
}

