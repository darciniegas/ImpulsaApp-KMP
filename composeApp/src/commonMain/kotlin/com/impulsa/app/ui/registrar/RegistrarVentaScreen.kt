package com.impulsa.app.ui.registrar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impulsa.app.core.Textos
import com.impulsa.app.core.fechaDesdeMillisUtc
import com.impulsa.app.core.millisUtcDeFecha
import com.impulsa.app.domain.FORMAS_DE_PAGO
import com.impulsa.app.ui.components.ContenidoCentrado
import com.impulsa.app.ui.components.ImpulsaScaffold
import com.impulsa.app.ui.theme.ImpulsaIcons
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** PANTALLA 7 — Formulario para registrar (o editar) una venta. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarVentaScreen(
    viewModel: RegistrarVentaViewModel,
    alVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var menuPagoAbierto by remember { mutableStateOf(false) }
    var calendarioAbierto by remember { mutableStateOf(false) }

    ImpulsaScaffold(
        titulo = if (estado.editando) "Editar venta" else "Registrar venta",
        alVolver = alVolver,
        snackbarHostState = snackbarHostState
    ) { padding ->
        ContenidoCentrado(modifier = Modifier.padding(padding), anchoMaximo = 640.dp) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CampoFormulario(
                    valor = estado.cliente,
                    alCambiar = viewModel::alCambiarCliente,
                    etiqueta = "Cliente *",
                    error = estado.errorCliente
                )
                CampoFormulario(
                    valor = estado.concepto,
                    alCambiar = viewModel::alCambiarConcepto,
                    etiqueta = "Concepto o producto *",
                    error = estado.errorConcepto
                )
                CampoFormulario(
                    valor = estado.valor,
                    alCambiar = viewModel::alCambiarValor,
                    etiqueta = "Valor de la venta *",
                    error = estado.errorValor,
                    prefijo = "$ ",
                    teclado = KeyboardType.Decimal
                )

                // ───── Forma de pago: menú desplegable ─────
                ExposedDropdownMenuBox(
                    expanded = menuPagoAbierto,
                    onExpandedChange = { menuPagoAbierto = it }
                ) {
                    OutlinedTextField(
                        value = estado.formaPago,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        label = { Text("Forma de pago *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuPagoAbierto) },
                        isError = estado.errorFormaPago != null,
                        supportingText = estado.errorFormaPago?.let { mensaje -> { Text(mensaje) } },
                        shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = menuPagoAbierto,
                        onDismissRequest = { menuPagoAbierto = false }
                    ) {
                        FORMAS_DE_PAGO.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    viewModel.alCambiarFormaPago(opcion)
                                    menuPagoAbierto = false
                                }
                            )
                        }
                    }
                }

                // ───── Fecha: campo de solo lectura que abre el calendario ─────
                Box {
                    OutlinedTextField(
                        value = estado.fecha,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Fecha") },
                        trailingIcon = {
                            IconButton(onClick = { calendarioAbierto = true }) {
                                Icon(ImpulsaIcons.Calendario, contentDescription = "Elegir fecha")
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                    // Capa transparente: el campo completo abre el calendario al tocarlo
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(end = 56.dp)
                            .clickable { calendarioAbierto = true }
                    )
                }

                CampoFormulario(
                    valor = estado.nota,
                    alCambiar = viewModel::alCambiarNota,
                    etiqueta = "Nota (opcional)",
                    error = null,
                    unaLinea = false
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.guardar(
                            alTerminar = { mensaje ->
                                scope.launch {
                                    launch { snackbarHostState.showSnackbar(mensaje) }
                                    delay(900)
                                    alVolver()
                                }
                            },
                            alFallar = {
                                scope.launch { snackbarHostState.showSnackbar("Revisa los campos marcados") }
                            }
                        )
                    },
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(ImpulsaIcons.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (estado.editando) "Guardar cambios" else Textos.GUARDAR, fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = alVolver,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(Textos.CANCELAR)
                }
            }
        }
    }

    // ───── Calendario Material 3 ─────
    if (calendarioAbierto) {
        val selector = rememberDatePickerState(initialSelectedDateMillis = millisUtcDeFecha(estado.fecha))
        DatePickerDialog(
            onDismissRequest = { calendarioAbierto = false },
            confirmButton = {
                TextButton(onClick = {
                    selector.selectedDateMillis?.let { viewModel.alCambiarFecha(fechaDesdeMillisUtc(it)) }
                    calendarioAbierto = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { calendarioAbierto = false }) { Text(Textos.CANCELAR) }
            }
        ) {
            DatePicker(state = selector, title = { Text("Fecha de la venta", modifier = Modifier.padding(start = 24.dp, top = 16.dp)) })
        }
    }
}

@Composable
private fun CampoFormulario(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    error: String?,
    prefijo: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    unaLinea: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        prefix = prefijo?.let { texto -> { Text(texto) } },
        isError = error != null,
        supportingText = error?.let { mensaje -> { Text(mensaje) } },
        singleLine = unaLinea,
        minLines = if (unaLinea) 1 else 3,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = teclado)
    )
}
