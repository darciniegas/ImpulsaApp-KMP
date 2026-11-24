package com.impulsa.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.impulsa.app.core.Textos
import com.impulsa.app.data.DatabaseDriverFactory
import com.impulsa.app.di.AppContainer
import com.impulsa.app.domain.ImpulsaRepository
import com.impulsa.app.navigation.BackStackEntry
import com.impulsa.app.navigation.Navigator
import com.impulsa.app.navigation.PlatformBackHandler
import com.impulsa.app.navigation.Route
import com.impulsa.app.navigation.esPrincipal
import com.impulsa.app.ui.acercade.AcercaDeScreen
import com.impulsa.app.ui.comisiones.ComisionesScreen
import com.impulsa.app.ui.comisiones.ComisionesViewModel
import com.impulsa.app.ui.desempeno.DesempenoScreen
import com.impulsa.app.ui.desempeno.DesempenoViewModel
import com.impulsa.app.ui.detalle.DetalleVentaScreen
import com.impulsa.app.ui.detalle.DetalleVentaViewModel
import com.impulsa.app.ui.inicio.InicioScreen
import com.impulsa.app.ui.inicio.InicioViewModel
import com.impulsa.app.ui.login.LoginScreen
import com.impulsa.app.ui.login.LoginViewModel
import com.impulsa.app.ui.perfil.PerfilScreen
import com.impulsa.app.ui.perfil.PerfilViewModel
import com.impulsa.app.ui.registrar.RegistrarVentaScreen
import com.impulsa.app.ui.registrar.RegistrarVentaViewModel
import com.impulsa.app.ui.theme.ImpulsaIcons
import com.impulsa.app.ui.theme.ImpulsaTheme
import com.impulsa.app.ui.ventas.VentasScreen
import com.impulsa.app.ui.ventas.VentasViewModel

/**
 * Punto de entrada COMPARTIDO de la interfaz. Lo llaman MainActivity (Android) y Main.kt (Desktop).
 */
@Composable
fun ImpulsaApp(driverFactory: DatabaseDriverFactory) {
    val contenedor = remember { AppContainer(driverFactory) }
    val navegador = remember {
        // Si hay una sesión guardada se entra directo al Inicio; si no, al Login.
        Navigator(if (contenedor.repositorio.haySesion()) Route.Inicio else Route.Login)
    }

    ImpulsaTheme {
        ImpulsaNavHost(repositorio = contenedor.repositorio, navegador = navegador)
    }
}

// ───────────────────────── Host de navegación ─────────────────────────

@Composable
private fun ImpulsaNavHost(repositorio: ImpulsaRepository, navegador: Navigator) {
    // Botón "atrás" del sistema (Android): retrocede en la pila compartida
    PlatformBackHandler(enabled = navegador.puedeRetroceder) { navegador.retroceder() }

    val entrada = navegador.actual

    MarcoAdaptativo(
        rutaActual = entrada.route,
        mostrarNavegacion = entrada.route.esPrincipal,
        alSeleccionar = navegador::seleccionarPrincipal
    ) {
        Crossfade(targetState = entrada, label = "navegacion") { actual ->
            PantallaDe(actual, navegador, repositorio)
        }
    }
}

/** Asocia cada ruta con su pantalla y su ViewModel. */
@Composable
private fun PantallaDe(entrada: BackStackEntry, nav: Navigator, repo: ImpulsaRepository) {
    when (val ruta = entrada.route) {
        Route.Login -> LoginScreen(
            viewModel = nav.viewModel(entrada) { LoginViewModel(repo) },
            alIniciarSesion = { nav.reiniciarEn(Route.Inicio) },
            alAbrirAcercaDe = { nav.navegar(Route.AcercaDe) }
        )

        Route.Inicio -> InicioScreen(
            viewModel = nav.viewModel(entrada) { InicioViewModel(repo) },
            alRegistrarVenta = { nav.navegar(Route.RegistrarVenta()) },
            alVerTodasLasVentas = { nav.navegar(Route.Ventas) },
            alAbrirVenta = { id -> nav.navegar(Route.DetalleVenta(id)) },
            alAbrirDesempeno = { nav.navegar(Route.Desempeno) },
            alAbrirComisiones = { nav.navegar(Route.Comisiones) }
        )

        Route.Ventas -> VentasScreen(
            viewModel = nav.viewModel(entrada) { VentasViewModel(repo) },
            alAbrirVenta = { id -> nav.navegar(Route.DetalleVenta(id)) },
            alRegistrarVenta = { nav.navegar(Route.RegistrarVenta()) }
        )

        Route.Desempeno -> DesempenoScreen(
            viewModel = nav.viewModel(entrada) { DesempenoViewModel(repo) }
        )

        Route.Comisiones -> ComisionesScreen(
            viewModel = nav.viewModel(entrada) { ComisionesViewModel(repo) }
        )

        Route.Perfil -> PerfilScreen(
            viewModel = nav.viewModel(entrada) { PerfilViewModel(repo) },
            alCerrarSesion = { nav.reiniciarEn(Route.Login) },
            alAbrirAcercaDe = { nav.navegar(Route.AcercaDe) }
        )

        is Route.RegistrarVenta -> RegistrarVentaScreen(
            viewModel = nav.viewModel(entrada) { RegistrarVentaViewModel(repo, ruta.ventaId) },
            alVolver = { nav.retroceder() }
        )

        is Route.DetalleVenta -> DetalleVentaScreen(
            viewModel = nav.viewModel(entrada) { DetalleVentaViewModel(repo, ruta.ventaId) },
            alVolver = { nav.retroceder() },
            alEditar = { id -> nav.navegar(Route.RegistrarVenta(id)) }
        )

        Route.AcercaDe -> AcercaDeScreen(alVolver = { nav.retroceder() })
    }
}

// ───────────────────────── Marco adaptativo ─────────────────────────

private data class DestinoPrincipal(val ruta: Route, val titulo: String, val icono: ImageVector)

private val DESTINOS = listOf(
    DestinoPrincipal(Route.Inicio, Textos.NAV_INICIO, ImpulsaIcons.Inicio),
    DestinoPrincipal(Route.Ventas, Textos.NAV_VENTAS, ImpulsaIcons.Ventas),
    DestinoPrincipal(Route.Desempeno, Textos.NAV_DESEMPENO, ImpulsaIcons.Desempeno),
    DestinoPrincipal(Route.Comisiones, Textos.NAV_COMISIONES, ImpulsaIcons.Comisiones),
    DestinoPrincipal(Route.Perfil, Textos.NAV_PERFIL, ImpulsaIcons.Perfil)
)

/**
 * Pantallas angostas (celular): barra de navegación inferior.
 * Pantallas anchas (ventana de escritorio): riel de navegación lateral.
 * El contenido y la navegación son los mismos en ambas plataformas.
 */
@Composable
private fun MarcoAdaptativo(
    rutaActual: Route,
    mostrarNavegacion: Boolean,
    alSeleccionar: (Route) -> Unit,
    contenido: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val ancha = maxWidth >= 840.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (mostrarNavegacion && ancha) {
                NavigationRail(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    DESTINOS.forEach { destino ->
                        NavigationRailItem(
                            selected = rutaActual == destino.ruta,
                            onClick = { alSeleccionar(destino.ruta) },
                            icon = { Icon(destino.icono, contentDescription = destino.titulo) },
                            label = { Text(destino.titulo) }
                        )
                    }
                }
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surface,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    if (mostrarNavegacion && !ancha) {
                        NavigationBar {
                            DESTINOS.forEach { destino ->
                                NavigationBarItem(
                                    selected = rutaActual == destino.ruta,
                                    onClick = { alSeleccionar(destino.ruta) },
                                    icon = { Icon(destino.icono, contentDescription = destino.titulo) },
                                    label = { Text(destino.titulo) }
                                )
                            }
                        }
                    }
                }
            ) { padding ->
                androidx.compose.foundation.layout.Box(modifier = Modifier.padding(padding)) {
                    contenido()
                }
            }
        }
    }
}
