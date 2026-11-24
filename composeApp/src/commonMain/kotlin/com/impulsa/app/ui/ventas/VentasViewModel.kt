package com.impulsa.app.ui.ventas

import com.impulsa.app.domain.ImpulsaRepository
import com.impulsa.app.domain.Venta
import com.impulsa.app.navigation.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class FiltroPago(val etiqueta: String, val clave: String) {
    TODAS("Todas", ""),
    TRANSFERENCIA("Transferencia", "Transferencia"),
    EFECTIVO("Efectivo", "Efectivo"),
    TARJETA("Tarjeta", "Tarjeta")   // coincide con tarjeta de crédito y débito
}

data class VentasEstado(
    val busqueda: String = "",
    val filtro: FiltroPago = FiltroPago.TODAS,
    val ventas: List<Venta> = emptyList()
) {
    val total: Double get() = ventas.sumOf { it.valor }
}

class VentasViewModel(repositorio: ImpulsaRepository) : ViewModel() {

    private val busqueda = MutableStateFlow("")
    private val filtro = MutableStateFlow(FiltroPago.TODAS)

    /** Se recalcula cada vez que cambian las ventas guardadas, el texto o el filtro. */
    val estado: StateFlow<VentasEstado> = combine(repositorio.ventas, busqueda, filtro) { ventas, texto, tipo ->
        val consulta = texto.trim()
        val resultado = ventas.filter { venta ->
            val coincideTexto = consulta.isEmpty() ||
                venta.cliente.contains(consulta, ignoreCase = true) ||
                venta.concepto.contains(consulta, ignoreCase = true)
            val coincidePago = tipo == FiltroPago.TODAS ||
                venta.formaPago.contains(tipo.clave, ignoreCase = true)
            coincideTexto && coincidePago
        }
        VentasEstado(busqueda = texto, filtro = tipo, ventas = resultado)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VentasEstado())

    fun alBuscar(texto: String) { busqueda.value = texto }

    fun alElegirFiltro(nuevo: FiltroPago) { filtro.value = nuevo }
}
