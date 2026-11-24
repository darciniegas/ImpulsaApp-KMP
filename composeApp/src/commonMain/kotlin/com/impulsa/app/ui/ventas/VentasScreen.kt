package com.impulsa.app.ui.ventas

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.impulsa.app.core.Textos
import com.impulsa.app.core.aMoneda
import com.impulsa.app.ui.components.ContenidoCentrado
import com.impulsa.app.ui.components.ImpulsaScaffold
import com.impulsa.app.ui.components.VentaItem
import com.impulsa.app.ui.theme.ImpulsaIcons

/** PANTALLA 3 — Listado de ventas con buscador y filtros. */
@Composable
fun VentasScreen(
    viewModel: VentasViewModel,
    alAbrirVenta: (String) -> Unit,
    alRegistrarVenta: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    ImpulsaScaffold(
        titulo = Textos.NAV_VENTAS,
        botonFlotante = {
            ExtendedFloatingActionButton(
                onClick = alRegistrarVenta,
                icon = { Icon(ImpulsaIcons.Add, contentDescription = null) },
                text = { Text("Nueva venta") }
            )
        }
    ) { padding ->
        ContenidoCentrado(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {

                // Buscador en tiempo real
                OutlinedTextField(
                    value = estado.busqueda,
                    onValueChange = viewModel::alBuscar,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(Textos.BUSCAR_VENTA) },
                    leadingIcon = { Icon(ImpulsaIcons.Buscar, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                // Filtros por forma de pago
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FiltroPago.values().forEach { opcion ->
                        FilterChip(
                            selected = estado.filtro == opcion,
                            onClick = { viewModel.alElegirFiltro(opcion) },
                            label = { Text(opcion.etiqueta) }
                        )
                    }
                }

                Text(
                    text = "${estado.ventas.size} ventas · ${estado.total.aMoneda()}",
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (estado.ventas.isEmpty()) {
                    Text(
                        text = "No hay ventas que coincidan con la búsqueda.",
                        modifier = Modifier.padding(top = 24.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        items(estado.ventas, key = { it.id }) { venta ->
                            VentaItem(venta = venta, alPulsar = { alAbrirVenta(venta.id) })
                        }
                    }
                }
            }
        }
    }
}
