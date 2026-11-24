package com.impulsa.app.domain

/** Una venta registrada por un asesor. */
data class Venta(
    val id: String,
    val cliente: String,
    val concepto: String,
    val valor: Double,
    val asesor: String,
    val formaPago: String,
    val fecha: String,
    val nota: String = ""
)

/** Un asesor comercial. */
data class Asesor(
    val id: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val ventas: Int,
    val ingresos: Double,
    val comision: Double,
    val meta: Double
) {
    /** Iniciales para el avatar, ej. "Ana García" -> "AG" */
    val iniciales: String
        get() = nombre.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .take(2)
            .joinToString("")

    /** Porcentaje de avance de la meta (0-999). */
    val porcentajeMeta: Int
        get() = if (meta <= 0.0) 0 else ((ingresos / meta) * 100).toInt().coerceAtMost(999)
}

/** Datos del formulario para crear o editar una venta. */
data class NuevaVenta(
    val cliente: String,
    val concepto: String,
    val valor: Double,
    val formaPago: String,
    val fecha: String,
    val nota: String
)

/** Formas de pago disponibles en el formulario. */
val FORMAS_DE_PAGO = listOf(
    "Efectivo",
    "Transferencia",
    "Tarjeta de crédito",
    "Tarjeta débito",
    "Crédito a 30 días"
)
