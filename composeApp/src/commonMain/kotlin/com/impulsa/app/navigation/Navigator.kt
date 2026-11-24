package com.impulsa.app.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Una entrada de la pila de navegación: la ruta + un id único (para asociarle su ViewModel). */
class BackStackEntry(val id: Int, val route: Route)

/**
 * Navegación COMPARTIDA entre Android y Desktop (vive en commonMain).
 *
 * - Mantiene la pila de pantallas (back stack) como estado de Compose.
 * - Guarda el ViewModel de cada entrada y lo libera cuando la entrada sale de la pila.
 * - La barra inferior (Android) y el riel lateral (Desktop) usan el mismo `seleccionarPrincipal`.
 */
class Navigator(rutaInicial: Route) {

    private var siguienteId = 0
    private val viewModels = HashMap<Int, ViewModel>()

    var pila: List<BackStackEntry> by mutableStateOf(listOf(crear(rutaInicial)))
        private set

    val actual: BackStackEntry get() = pila.last()
    val puedeRetroceder: Boolean get() = pila.size > 1

    /** Apila una nueva pantalla encima de la actual. */
    fun navegar(ruta: Route) {
        pila = pila + crear(ruta)
    }

    /** Vuelve a la pantalla anterior. Devuelve false si ya estaba en la raíz. */
    fun retroceder(): Boolean {
        if (!puedeRetroceder) return false
        val saliente = pila.last()
        pila = pila.dropLast(1)
        liberar(saliente)
        return true
    }

    /** Vacía la pila y deja solo `ruta` (ej. login -> inicio, o cerrar sesión -> login). */
    fun reiniciarEn(ruta: Route) {
        val anteriores = pila
        pila = listOf(crear(ruta))
        anteriores.forEach(::liberar)
    }

    /**
     * Cambio de pestaña principal (barra inferior / riel).
     * La pila queda como [Inicio] o [Inicio, destino], de modo que "atrás" desde cualquier
     * pestaña lleva a Inicio.
     */
    fun seleccionarPrincipal(ruta: Route) {
        if (actual.route == ruta) return
        val anteriores = pila
        val inicio = anteriores.firstOrNull { it.route == Route.Inicio } ?: crear(Route.Inicio)
        pila = if (ruta == Route.Inicio) listOf(inicio) else listOf(inicio, crear(ruta))
        anteriores.filter { it !in pila }.forEach(::liberar)
    }

    /** Devuelve el ViewModel de una entrada, creándolo la primera vez. */
    @Suppress("UNCHECKED_CAST")
    fun <T : ViewModel> viewModel(entrada: BackStackEntry, fabrica: () -> T): T =
        viewModels.getOrPut(entrada.id, fabrica) as T

    private fun crear(ruta: Route) = BackStackEntry(siguienteId++, ruta)

    private fun liberar(entrada: BackStackEntry) {
        viewModels.remove(entrada.id)?.clear()
    }
}
