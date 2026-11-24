package com.impulsa.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.impulsa.app.core.aMoneda
import com.impulsa.app.domain.Asesor
import com.impulsa.app.domain.Venta
import com.impulsa.app.ui.theme.ImpulsaIcons

/** Ancho máximo del contenido: en escritorio la app no se estira a todo el ancho de la ventana. */
val ANCHO_MAXIMO_CONTENIDO = 840.dp

/** Centra el contenido y limita su ancho (clave para que Desktop se vea bien en ventanas grandes). */
@Composable
fun ContenidoCentrado(
    modifier: Modifier = Modifier,
    anchoMaximo: Dp = ANCHO_MAXIMO_CONTENIDO,
    contenido: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Box(modifier = Modifier.widthIn(max = anchoMaximo).fillMaxSize()) {
            contenido()
        }
    }
}

/** Hace que un elemento "suba" `dp` sobre el anterior (tarjeta de KPIs sobre el encabezado). */
fun Modifier.superponerArriba(dp: Dp): Modifier = layout { medible, restricciones ->
    val colocable = medible.measure(restricciones)
    val px = dp.roundToPx()
    layout(colocable.width, (colocable.height - px).coerceAtLeast(0)) {
        colocable.place(0, -px)
    }
}

// ───────────────────────── Estructura de pantalla ─────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpulsaScaffold(
    titulo: String,
    alVolver: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState? = null,
    acciones: @Composable RowScope.() -> Unit = {},
    botonFlotante: @Composable () -> Unit = {},
    contenido: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    if (alVolver != null) {
                        IconButton(onClick = alVolver) {
                            Icon(ImpulsaIcons.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = acciones,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { if (snackbarHostState != null) SnackbarHost(snackbarHostState) },
        floatingActionButton = botonFlotante,
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        content = contenido
    )
}

// ───────────────────────── Tarjetas y etiquetas ─────────────────────────

/** Tarjeta estándar de Impulsa (esquinas de 20 dp, elevación 1 dp). */
@Composable
fun TarjetaImpulsa(
    modifier: Modifier = Modifier,
    alPulsar: (() -> Unit)? = null,
    colorFondo: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    radio: Dp = 20.dp,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(radio)
    val colores = CardDefaults.elevatedCardColors(containerColor = colorFondo)
    val elevacion = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    if (alPulsar != null) {
        ElevatedCard(
            onClick = alPulsar,
            modifier = modifier,
            shape = forma,
            colors = colores,
            elevation = elevacion,
            content = contenido
        )
    } else {
        ElevatedCard(
            modifier = modifier,
            shape = forma,
            colors = colores,
            elevation = elevacion,
            content = contenido
        )
    }
}

/** Texto pequeño en mayúsculas sobre un dato (equivale al estilo "TextoEtiqueta"). */
@Composable
fun Etiqueta(
    texto: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = texto.uppercase(),
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        letterSpacing = 0.06.em,
        color = color
    )
}

/** Pastilla de texto con borde (equivale a los Chip no clicables del prototipo). */
@Composable
fun ChipEtiqueta(
    texto: String,
    modifier: Modifier = Modifier,
    tamanoTexto: TextUnit = 12.sp
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontSize = tamanoTexto,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun Avatar(
    iniciales: String,
    tamano: Dp,
    tamanoTexto: TextUnit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciales,
            fontSize = tamanoTexto,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun BarraProgreso(
    porcentaje: Int,
    grosor: Dp,
    modifier: Modifier = Modifier
) {
    val avance = (porcentaje.coerceIn(0, 100)) / 100f
    LinearProgressIndicator(
        progress = { avance },
        modifier = modifier
            .fillMaxWidth()
            .height(grosor)
            .clip(RoundedCornerShape(grosor)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

// ───────────────────────── Filas de lista ─────────────────────────

/** Fila de la lista de ventas (item_venta.xml). */
@Composable
fun VentaItem(
    venta: Venta,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier
) {
    TarjetaImpulsa(modifier = modifier.fillMaxWidth(), alPulsar = alPulsar, radio = 18.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = venta.concepto,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = venta.cliente,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${venta.fecha} · ${venta.asesor}",
                    modifier = Modifier.padding(top = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = venta.valor.aMoneda(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                ChipEtiqueta(
                    texto = venta.formaPago,
                    modifier = Modifier.padding(top = 4.dp),
                    tamanoTexto = 11.sp
                )
            }
        }
    }
}

enum class ModoAsesor { RANKING, COMISION }

/** Fila del ranking de asesores / comisiones (item_asesor.xml). */
@Composable
fun AsesorItem(
    asesor: Asesor,
    posicion: Int,
    modo: ModoAsesor,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier
) {
    TarjetaImpulsa(modifier = modifier.fillMaxWidth(), alPulsar = alPulsar, radio = 18.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$posicion",
                    modifier = Modifier.width(26.dp),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Avatar(iniciales = asesor.iniciales, tamano = 40.dp, tamanoTexto = 14.sp)
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(
                        text = asesor.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (modo == ModoAsesor.RANKING) {
                            "${asesor.ventas} ventas · meta ${asesor.porcentajeMeta}%"
                        } else {
                            "Sobre ${asesor.ingresos.aMoneda()} vendidos"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = if (modo == ModoAsesor.RANKING) asesor.ingresos.aMoneda() else asesor.comision.aMoneda(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            BarraProgreso(
                porcentaje = asesor.porcentajeMeta,
                grosor = 6.dp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
