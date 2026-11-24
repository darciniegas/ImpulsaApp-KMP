package com.impulsa.app.ui.detalle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.impulsa.app.core.Textos
import com.impulsa.app.core.aMoneda
import com.impulsa.app.ui.components.ChipEtiqueta
import com.impulsa.app.ui.components.ContenidoCentrado
import com.impulsa.app.ui.components.Etiqueta
import com.impulsa.app.ui.components.ImpulsaScaffold
import com.impulsa.app.ui.components.TarjetaImpulsa
import com.impulsa.app.ui.theme.ImpulsaIcons

/** PANTALLA 8 — Detalle de una venta (con editar y eliminar). */
@Composable
fun DetalleVentaScreen(
    viewModel: DetalleVentaViewModel,
    alVolver: () -> Unit,
    alEditar: (String) -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val venta = estado.venta
    var confirmarEliminar by remember { mutableStateOf(false) }
    val colores = MaterialTheme.colorScheme

    ImpulsaScaffold(
        titulo = "Detalle de venta",
        alVolver = alVolver,
        acciones = {
            if (venta != null) {
                IconButton(onClick = { alEditar(venta.id) }) {
                    Icon(ImpulsaIcons.Editar, contentDescription = "Editar venta")
                }
                IconButton(onClick = { confirmarEliminar = true }) {
                    Icon(ImpulsaIcons.Eliminar, contentDescription = "Eliminar venta")
                }
            }
        }
    ) { padding ->
        ContenidoCentrado(modifier = Modifier.padding(padding), anchoMaximo = 640.dp) {
            if (venta == null) {
                Text(
                    text = if (estado.cargando) "Cargando…" else "Venta no encontrada",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colores.onSurfaceVariant
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // ───── Encabezado con el monto ─────
                    TarjetaImpulsa(modifier = Modifier.fillMaxWidth(), colorFondo = colores.primaryContainer) {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                            Etiqueta(venta.id, color = colores.onPrimaryContainer)
                            Text(
                                text = venta.valor.aMoneda(),
                                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = colores.onPrimaryContainer
                            )
                            ChipEtiqueta("Venta confirmada")
                        }
                    }

                    // ───── Datos ─────
                    TarjetaImpulsa(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                            Dato("Cliente", venta.cliente)
                            Dato("Concepto", venta.concepto)
                            Dato("Asesor responsable", venta.asesor)
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) { Dato("Forma de pago", venta.formaPago) }
                                Column(modifier = Modifier.weight(1f)) { Dato("Fecha", venta.fecha) }
                            }
                        }
                    }

                    // ───── Nota (solo si existe) ─────
                    if (venta.nota.isNotBlank()) {
                        TarjetaImpulsa(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            colorFondo = colores.secondaryContainer
                        ) {
                            Text(
                                text = venta.nota,
                                modifier = Modifier.fillMaxWidth().padding(18.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colores.onSecondaryContainer
                            )
                        }
                    }

                    Button(
                        onClick = alVolver,
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 16.dp).height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(Textos.VOLVER)
                    }
                }
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar venta") },
            text = { Text("Se eliminará esta venta y se descontará de tus acumulados. ¿Deseas continuar?") },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text(Textos.CANCELAR) }
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    viewModel.eliminar(alTerminar = alVolver)
                }) { Text("Eliminar") }
            }
        )
    }
}

@Composable
private fun Dato(etiqueta: String, valor: String) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Etiqueta(etiqueta)
        Text(
            text = valor,
            modifier = Modifier.padding(top = 2.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
