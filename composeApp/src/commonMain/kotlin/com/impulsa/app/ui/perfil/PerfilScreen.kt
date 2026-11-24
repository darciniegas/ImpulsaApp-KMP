package com.impulsa.app.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impulsa.app.core.Textos
import com.impulsa.app.core.aMoneda
import com.impulsa.app.ui.components.Avatar
import com.impulsa.app.ui.components.ChipEtiqueta
import com.impulsa.app.ui.components.ContenidoCentrado
import com.impulsa.app.ui.components.Etiqueta
import com.impulsa.app.ui.components.ImpulsaScaffold
import com.impulsa.app.ui.components.TarjetaImpulsa
import com.impulsa.app.ui.theme.ImpulsaIcons

/** PANTALLA 6 — Perfil del usuario, preferencias y cierre de sesión. */
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    alCerrarSesion: () -> Unit,
    alAbrirAcercaDe: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val usuario = estado.usuario
    var confirmarCierre by remember { mutableStateOf(false) }
    val colores = MaterialTheme.colorScheme

    ImpulsaScaffold(titulo = Textos.NAV_PERFIL) { padding ->
        ContenidoCentrado(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ───── Cabecera del perfil ─────
                Avatar(iniciales = usuario?.iniciales.orEmpty(), tamano = 88.dp, tamanoTexto = 32.sp)
                Text(
                    text = usuario?.nombre.orEmpty(),
                    modifier = Modifier.padding(top = 12.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colores.onSurface
                )
                ChipEtiqueta("Asesor/a comercial", modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))

                // ───── Datos de contacto ─────
                TarjetaImpulsa(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp)) {
                        FilaContacto(ImpulsaIcons.Correo, usuario?.correo.orEmpty())
                        Box(Modifier.fillMaxWidth().height(1.dp).background(colores.outlineVariant))
                        FilaContacto(ImpulsaIcons.Telefono, usuario?.telefono.orEmpty())
                    }
                }

                // ───── Resumen de actividad ─────
                TarjetaImpulsa(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Dato(usuario?.ventas?.toString().orEmpty(), "Ventas")
                        Dato(usuario?.ingresos?.aMoneda().orEmpty(), "Ingresos")
                    }
                }

                // ───── Preferencias (guardadas en la base de datos) ─────
                TarjetaImpulsa(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp)) {
                        FilaSwitch("Notificaciones de nuevas ventas", estado.notificaciones, viewModel::alCambiarNotificaciones)
                        FilaSwitch("Resumen semanal por correo", estado.resumenSemanal, viewModel::alCambiarResumenSemanal)
                    }
                }

                // ───── Acerca de ─────
                TarjetaImpulsa(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    alPulsar = alAbrirAcercaDe
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(ImpulsaIcons.Info, null, tint = colores.primary, modifier = Modifier.size(24.dp))
                        Text(
                            text = Textos.ACERCA_DE,
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colores.onSurface
                        )
                        Icon(ImpulsaIcons.Flecha, null, tint = colores.outline)
                    }
                }

                // ───── Cerrar sesión ─────
                OutlinedButton(
                    onClick = { confirmarCierre = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(ImpulsaIcons.Salir, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(Textos.CERRAR_SESION)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (confirmarCierre) {
        AlertDialog(
            onDismissRequest = { confirmarCierre = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que deseas salir de tu cuenta?") },
            dismissButton = {
                TextButton(onClick = { confirmarCierre = false }) { Text(Textos.CANCELAR) }
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmarCierre = false
                    viewModel.cerrarSesion(alTerminar = alCerrarSesion)
                }) { Text(Textos.CERRAR_SESION) }
            }
        )
    }
}

@Composable
private fun FilaContacto(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(
            text = texto,
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun Dato(valor: String, etiqueta: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Etiqueta(etiqueta)
    }
}

@Composable
private fun FilaSwitch(texto: String, marcado: Boolean, alCambiar: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = texto,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(checked = marcado, onCheckedChange = alCambiar)
    }
}
