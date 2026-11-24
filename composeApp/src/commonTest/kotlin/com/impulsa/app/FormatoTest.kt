package com.impulsa.app

import com.impulsa.app.core.aMoneda
import com.impulsa.app.core.aPorcentajeUnDecimal
import com.impulsa.app.core.esCorreoValido
import com.impulsa.app.core.formatearFecha
import com.impulsa.app.core.millisUtcDeFecha
import com.impulsa.app.core.fechaDesdeMillisUtc
import com.impulsa.app.domain.Asesor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FormatoTest {
    @Test fun moneda_agrupa_miles_con_punto() {
        assertEquals("$ 54.200", 54200.0.aMoneda())
        assertEquals("$ 980", 980.0.aMoneda())
        assertEquals("$ 1.234.567", 1234567.0.aMoneda())
    }

    @Test fun porcentaje_con_coma() {
        assertEquals("8,0", 8.0.aPorcentajeUnDecimal())
        assertEquals("6,5", 6.5.aPorcentajeUnDecimal())
    }

    @Test fun correo_valido_e_invalido() {
        assertTrue("ana@impulsa.co".esCorreoValido())
        assertFalse("ana@".esCorreoValido())
        assertFalse("sin-arroba".esCorreoValido())
    }

    @Test fun fecha_formato_y_viaje_de_ida_y_vuelta() {
        assertEquals("05/03/2026", formatearFecha(5, 3, 2026))
        val millis = millisUtcDeFecha("23/08/2026")
        assertNotNull(millis)
        assertEquals("23/08/2026", fechaDesdeMillisUtc(millis))
        assertEquals(null, millisUtcDeFecha("no es fecha"))
    }

    @Test fun asesor_iniciales_y_porcentaje_de_meta() {
        val a = Asesor("a1", "Ana García", "ana@impulsa.co", "1", 18, 54200.0, 4336.0, 50000.0)
        assertEquals("AG", a.iniciales)
        assertEquals(108, a.porcentajeMeta)
    }
}
