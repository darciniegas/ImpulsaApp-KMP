package com.impulsa.app

import com.impulsa.app.navigation.Navigator
import com.impulsa.app.navigation.Route
import com.impulsa.app.navigation.ViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class NavigatorTest {

    private class VmFalso : ViewModel() { var limpio = false; override fun onCleared() { limpio = true } }

    @Test fun login_a_inicio_vacia_la_pila() {
        val nav = Navigator(Route.Login)
        nav.reiniciarEn(Route.Inicio)
        assertEquals(Route.Inicio, nav.actual.route)
        assertFalse(nav.puedeRetroceder)
    }

    @Test fun navegar_y_retroceder() {
        val nav = Navigator(Route.Inicio)
        nav.navegar(Route.DetalleVenta("V-1"))
        assertEquals(Route.DetalleVenta("V-1"), nav.actual.route)
        assertTrue(nav.retroceder())
        assertEquals(Route.Inicio, nav.actual.route)
        assertFalse(nav.retroceder())
    }

    @Test fun pestanas_principales_regresan_a_inicio() {
        val nav = Navigator(Route.Inicio)
        nav.seleccionarPrincipal(Route.Ventas)
        nav.seleccionarPrincipal(Route.Perfil)
        assertEquals(Route.Perfil, nav.actual.route)
        assertEquals(2, nav.pila.size)
        nav.retroceder()
        assertEquals(Route.Inicio, nav.actual.route)
    }

    @Test fun viewmodel_se_conserva_y_se_libera_al_salir_de_la_pila() {
        val nav = Navigator(Route.Inicio)
        nav.navegar(Route.Ventas)
        val entrada = nav.actual
        val vm = nav.viewModel(entrada) { VmFalso() }
        assertSame(vm, nav.viewModel(entrada) { VmFalso() })
        nav.retroceder()
        assertTrue(vm.limpio)
    }
}
