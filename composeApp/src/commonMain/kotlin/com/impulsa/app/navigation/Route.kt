package com.impulsa.app.navigation

/**
 * Todos los destinos de la app. Reemplaza al antiguo nav_graph.xml del Navigation Component.
 * Las rutas con argumentos son `data class` (ej. el id de la venta).
 */
sealed interface Route {
    data object Login : Route
    data object Inicio : Route
    data object Ventas : Route
    data object Desempeno : Route
    data object Comisiones : Route
    data object Perfil : Route
    data class RegistrarVenta(val ventaId: String? = null) : Route   // null = crear, id = editar
    data class DetalleVenta(val ventaId: String) : Route
    data object AcercaDe : Route
}

/** Destinos principales: los que aparecen en la barra inferior / riel lateral. */
val RUTAS_PRINCIPALES: List<Route> = listOf(
    Route.Inicio,
    Route.Ventas,
    Route.Desempeno,
    Route.Comisiones,
    Route.Perfil
)

val Route.esPrincipal: Boolean get() = this in RUTAS_PRINCIPALES
