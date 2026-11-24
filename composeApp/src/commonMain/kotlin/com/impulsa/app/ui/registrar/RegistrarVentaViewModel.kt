package com.impulsa.app.ui.registrar

import com.impulsa.app.core.fechaHoy
import com.impulsa.app.domain.ImpulsaRepository
import com.impulsa.app.domain.NuevaVenta
import com.impulsa.app.navigation.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistrarEstado(
    val editando: Boolean = false,
    val cliente: String = "",
    val concepto: String = "",
    val valor: String = "",
    val formaPago: String = "",
    val fecha: String = fechaHoy(),
    val nota: String = "",
    val errorCliente: String? = null,
    val errorConcepto: String? = null,
    val errorValor: String? = null,
    val errorFormaPago: String? = null,
    val guardando: Boolean = false
)

/**
 * Formulario compartido para CREAR (ventaId = null) y EDITAR una venta existente.
 */
class RegistrarVentaViewModel(
    private val repositorio: ImpulsaRepository,
    private val ventaId: String?
) : ViewModel() {

    private val _estado = MutableStateFlow(RegistrarEstado(editando = ventaId != null))
    val estado: StateFlow<RegistrarEstado> = _estado.asStateFlow()

    init {
        if (ventaId != null) {
            viewModelScope.launch {
                repositorio.venta(ventaId).first()?.let { venta ->
                    _estado.update {
                        it.copy(
                            cliente = venta.cliente,
                            concepto = venta.concepto,
                            valor = if (venta.valor % 1.0 == 0.0) venta.valor.toLong().toString() else venta.valor.toString(),
                            formaPago = venta.formaPago,
                            fecha = venta.fecha,
                            nota = venta.nota
                        )
                    }
                }
            }
        }
    }

    fun alCambiarCliente(v: String) = _estado.update { it.copy(cliente = v, errorCliente = null) }
    fun alCambiarConcepto(v: String) = _estado.update { it.copy(concepto = v, errorConcepto = null) }
    fun alCambiarValor(v: String) = _estado.update { it.copy(valor = v, errorValor = null) }
    fun alCambiarFormaPago(v: String) = _estado.update { it.copy(formaPago = v, errorFormaPago = null) }
    fun alCambiarFecha(v: String) = _estado.update { it.copy(fecha = v) }
    fun alCambiarNota(v: String) = _estado.update { it.copy(nota = v) }

    /**
     * Valida y guarda. `alTerminar(mensaje)` se llama solo si se guardó;
     * `alFallar()` si hay campos inválidos.
     */
    fun guardar(alTerminar: (String) -> Unit, alFallar: () -> Unit) {
        val e = _estado.value
        val cliente = e.cliente.trim()
        val concepto = e.concepto.trim()
        val valorTexto = e.valor.trim().replace(',', '.')
        val valor = valorTexto.toDoubleOrNull()
        val formaPago = e.formaPago.trim()

        val errorCliente = if (cliente.isEmpty()) "Ingresa el cliente" else null
        val errorConcepto = if (concepto.isEmpty()) "Ingresa el concepto" else null
        val errorValor = when {
            valorTexto.isEmpty() -> "Ingresa el valor"
            valor == null || valor <= 0.0 -> "Valor inválido"
            else -> null
        }
        val errorFormaPago = if (formaPago.isEmpty()) "Selecciona la forma de pago" else null

        _estado.update {
            it.copy(
                errorCliente = errorCliente,
                errorConcepto = errorConcepto,
                errorValor = errorValor,
                errorFormaPago = errorFormaPago
            )
        }
        if (errorCliente != null || errorConcepto != null || errorValor != null || errorFormaPago != null || valor == null) {
            alFallar()
            return
        }

        val datos = NuevaVenta(
            cliente = cliente,
            concepto = concepto,
            valor = valor,
            formaPago = formaPago,
            fecha = e.fecha,
            nota = e.nota.trim()
        )

        _estado.update { it.copy(guardando = true) }
        viewModelScope.launch {
            if (ventaId != null) {
                repositorio.actualizarVenta(ventaId, datos)
                alTerminar("Venta actualizada correctamente")
            } else {
                val asesor = repositorio.usuarioActual.first()
                if (asesor == null) {
                    _estado.update { it.copy(guardando = false) }
                    alFallar()
                    return@launch
                }
                repositorio.registrarVenta(datos, asesor)
                alTerminar("¡Venta registrada correctamente!")
            }
        }
    }
}
