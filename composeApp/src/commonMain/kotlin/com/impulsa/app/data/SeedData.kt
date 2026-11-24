package com.impulsa.app.data

import com.impulsa.app.domain.Asesor
import com.impulsa.app.domain.Venta

/**
 * Datos iniciales. Son exactamente los datos de ejemplo del prototipo Android original
 * (antes en `object Repositorio`). Se cargan UNA sola vez, cuando la base está vacía.
 */
object SeedData {

    val asesores = listOf(
        Asesor("a1", "Daniel Arciniegas",       "daniel@impulsa.co",  "+57 300 123 4567", 18, 54200.0, 4336.0, 50000.0),
        Asesor("a2", "Katherine Tamayo",        "kathe@impulsa.co",   "+57 300 234 5678", 12, 38400.0, 2880.0, 50000.0),
        Asesor("a3", "Maria Jose Peña",         "majo@impulsa.co",    "+57 300 345 6789", 23, 72600.0, 5808.0, 60000.0),
        Asesor("a4", "María Torres", "maria@impulsa.co",   "+57 300 456 7890",  9, 22100.0, 1326.0, 40000.0),
        Asesor("a5", "Carlos Vega",  "carlos@impulsa.co",  "+57 300 567 8901", 15, 47800.0, 3824.0, 50000.0)
    )

    /** De la más reciente a la más antigua (mismo orden que el prototipo). */
    val ventas = listOf(
        Venta("V-2408-001", "Inversiones Solaris SA", "Plan Empresarial Anual", 8500.0, "Majo", "Transferencia", "23/08/2026", "Contrato firmado. Inicio en septiembre."),
        Venta("V-2408-002", "Ana Beatriz Córdoba", "Servicio Premium Mensual", 2800.0, "Daniel", "Tarjeta de crédito", "22/08/2026"),
        Venta("V-2408-003", "Distribuidora Noreste", "Consultoría 5 sesiones", 6200.0, "Majo", "Transferencia", "21/08/2026"),
        Venta("V-2408-004", "Jorge Santana", "Plan Básico Trimestral", 1950.0, "Kathe", "Efectivo", "20/08/2026"),
        Venta("V-2408-005", "Grupo Empresarial Vega", "Soporte y Mantenimiento", 4400.0, "Carlos Vega", "Tarjeta débito", "19/08/2026"),
        Venta("V-2408-006", "Clínica Bienestar", "Plan Premium Anual", 12000.0, "Daniel", "Transferencia", "18/08/2026"),
        Venta("V-2408-007", "René Montoya", "Servicio Básico", 980.0, "María Torres", "Efectivo", "17/08/2026"),
        Venta("V-2408-008", "Tech Solutions SAS", "Licencias Software x10", 15500.0, "Majo", "Transferencia", "15/08/2026", "Incluye instalación y capacitación.")
    )

    /** Asesor que usa la sesión cuando el correo no corresponde a ninguno (comportamiento del prototipo). */
    const val ID_USUARIO_POR_DEFECTO = "a1"

    const val PREFIJO_ID_VENTA = "V-2408-"
}
