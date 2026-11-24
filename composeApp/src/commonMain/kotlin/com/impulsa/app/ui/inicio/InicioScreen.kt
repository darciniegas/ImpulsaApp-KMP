package com.impulsa.app.ui.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.impulsa.app.core.Textos
import com.impulsa.app.core.aMoneda
import com.impulsa.app.ui.components.Avatar
import com.impulsa.app.ui.components.BarraProgreso
import com.impulsa.app.ui.components.ChipEtiqueta
import com.impulsa.app.ui.components.ContenidoCentrado
import com.impulsa.app.ui.components.Etiqueta
import com.impulsa.app.ui.components.TarjetaImpulsa
import com.impulsa.app.ui.components.VentaItem
import com.impulsa.app.ui.components.superponerArriba
import com.impulsa.app.ui.theme.ImpulsaBrand
import com.impulsa.app.ui.theme.ImpulsaIcons

private enum class Periodo(val etiqueta: String) { HOY("Hoy"), SEMANA("Semana"), MES("Mes") }

/** PANTALLA 2 — Dashboard / Inicio: KPIs, avance de meta y últimas ventas. */
@Composable
fun InicioScreen(
    viewModel: InicioViewModel,
    alRegistrarVenta: () -> Unit,
    alVerTodasLasVentas: () -> Unit,
    alAbrirVenta: (String) -> Unit,
    alAbrirDesempeno: () -> Unit,
    alAbrirComisiones: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val usuario = estado.usuario
    var periodo by remember { mutableStateOf(Periodo.MES) }

    ContenidoCentrado {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // ───── Encabezado con degradado de marca ─────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(Brush.linearGradient(ImpulsaBrand.GradienteEncabezado))
                    .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 44.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "BIENVENIDO/A",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 0.1.em,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = usuario?.nombre.orEmpty(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Avatar(iniciales = usuario?.iniciales.orEmpty(), tamano = 48.dp, tamanoTexto = 16.sp)
                }

                // Filtro por periodo (selección visual, igual que en el prototipo)
                Row(
                    modifier = Modifier.padding(top = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Periodo.values().forEach { opcion ->
                        FilterChip(
                            selected = periodo == opcion,
                            onClick = { periodo = opcion },
                            label = { Text(opcion.etiqueta) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.14f),
                                labelColor = Color.White,
                                selectedContainerColor = Color.White,
                                selectedLabelColor = ImpulsaBrand.Indigo
                            )
                        )
                    }
                }
            }

            // ───── KPIs superpuestos al encabezado ─────
            TarjetaImpulsa(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().superponerArriba(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Kpi(usuario?.ingresos?.aMoneda().orEmpty(), "Ventas", Modifier.weight(1f))
                    DivisorVertical()
                    Kpi(usuario?.comision?.aMoneda().orEmpty(), "Comisión", Modifier.weight(1f))
                    DivisorVertical()
                    Kpi(usuario?.let { "${it.porcentajeMeta}%" }.orEmpty(), "Meta", Modifier.weight(1f))
                }
            }

            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 12.dp)) {

                // ───── Avance de meta ─────
                TarjetaImpulsa(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Avance de meta — Agosto",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            ChipEtiqueta("${usuario?.porcentajeMeta ?: 0}%")
                        }
                        Text(
                            text = if (usuario != null) "${usuario.ingresos.aMoneda()} de ${usuario.meta.aMoneda()}" else "",
                            modifier = Modifier.padding(top = 2.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        BarraProgreso(
                            porcentaje = usuario?.porcentajeMeta ?: 0,
                            grosor = 8.dp,
                            modifier = Modifier.padding(top = 14.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ───── Botón grande: registrar venta ─────
                Button(
                    onClick = alRegistrarVenta,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(ImpulsaIcons.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(Textos.REGISTRAR_VENTA, fontSize = 16.sp)
                }

                Spacer(Modifier.height(18.dp))

                // ───── Ventas recientes ─────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ventas recientes",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = alVerTodasLasVentas) { Text(Textos.VER_TODAS) }
                }

                if (estado.ventasRecientes.isEmpty()) {
                    Text(
                        text = "Aún no tienes ventas registradas.",
                        modifier = Modifier.padding(vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        estado.ventasRecientes.forEach { venta ->
                            VentaItem(venta = venta, alPulsar = { alAbrirVenta(venta.id) })
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ───── Accesos rápidos ─────
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccesoRapido(
                        modifier = Modifier.weight(1f),
                        icono = { Icon(ImpulsaIcons.Tendencia, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)) },
                        titulo = "Mi desempeño",
                        subtitulo = "Ver métricas",
                        alPulsar = alAbrirDesempeno
                    )
                    AccesoRapido(
                        modifier = Modifier.weight(1f),
                        icono = { Icon(ImpulsaIcons.Comisiones, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(24.dp)) },
                        titulo = "Comisiones",
                        subtitulo = "Ver detalle",
                        alPulsar = alAbrirComisiones
                    )
                }
            }
        }
    }
}

@Composable
private fun Kpi(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Etiqueta(etiqueta)
    }
}

@Composable
private fun DivisorVertical() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

@Composable
private fun AccesoRapido(
    icono: @Composable () -> Unit,
    titulo: String,
    subtitulo: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier
) {
    TarjetaImpulsa(modifier = modifier, alPulsar = alPulsar) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            icono()
            Text(
                text = titulo,
                modifier = Modifier.padding(top = 10.dp),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
