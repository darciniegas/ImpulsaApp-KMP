package com.impulsa.app.ui.comisiones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.impulsa.app.core.Textos
import com.impulsa.app.core.aMoneda
import com.impulsa.app.core.aPorcentajeUnDecimal
import com.impulsa.app.ui.components.AsesorItem
import com.impulsa.app.ui.components.ContenidoCentrado
import com.impulsa.app.ui.components.Etiqueta
import com.impulsa.app.ui.components.ImpulsaScaffold
import com.impulsa.app.ui.components.ModoAsesor
import com.impulsa.app.ui.components.TarjetaImpulsa
import kotlinx.coroutines.launch

/** PANTALLA 5 — Comisiones del usuario y del equipo. */
@Composable
fun ComisionesScreen(viewModel: ComisionesViewModel) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val usuario = estado.usuario
    val colores = MaterialTheme.colorScheme

    ImpulsaScaffold(titulo = Textos.NAV_COMISIONES, snackbarHostState = snackbarHostState) { padding ->
        ContenidoCentrado(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // ───── Tarjeta destacada con la comisión del mes ─────
                TarjetaImpulsa(modifier = Modifier.fillMaxWidth(), colorFondo = colores.primary) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Etiqueta("Comisión estimada · Agosto", color = colores.onPrimary.copy(alpha = 0.85f))
                        Text(
                            text = usuario?.comision?.aMoneda().orEmpty(),
                            modifier = Modifier.padding(top = 6.dp),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = colores.onPrimary
                        )
                        Text(
                            text = if (usuario != null) {
                                "${estado.porcentajeUsuario.aPorcentajeUnDecimal()}% sobre ${usuario.ingresos.aMoneda()} en ventas"
                            } else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colores.onPrimary.copy(alpha = 0.85f)
                        )
                        Button(
                            onClick = {
                                scope.launch { snackbarHostState.showSnackbar("Solicitud enviada a administración") }
                            },
                            modifier = Modifier.padding(top = 16.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colores.onPrimary,
                                contentColor = colores.primary
                            )
                        ) {
                            Text("Solicitar liquidación")
                        }
                    }
                }

                // ───── Esquema de comisiones ─────
                TarjetaImpulsa(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                        Text(
                            text = "Esquema de comisiones",
                            style = MaterialTheme.typography.titleMedium,
                            color = colores.onSurface
                        )
                        listOf(
                            "• Hasta $ 20.000 en ventas → 5%",
                            "• De $ 20.001 a $ 50.000 → 6,5%",
                            "• Más de $ 50.000 → 8%"
                        ).forEach { linea ->
                            Text(
                                text = linea,
                                modifier = Modifier.padding(top = 6.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colores.onSurfaceVariant
                            )
                        }
                    }
                }

                // ───── Comisiones del equipo ─────
                Text(
                    text = "Comisiones del equipo",
                    modifier = Modifier.padding(top = 16.dp, bottom = 10.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = colores.onSurface
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    estado.equipo.forEachIndexed { indice, asesor ->
                        AsesorItem(
                            asesor = asesor,
                            posicion = indice + 1,
                            modo = ModoAsesor.COMISION,
                            alPulsar = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Comisión de ${asesor.nombre}: ${asesor.comision.aMoneda()}")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
