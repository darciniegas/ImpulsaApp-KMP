package com.impulsa.app.core

/**
 * Información de la pantalla "Acerca de / Créditos".
 *
 * ⚠️ EDITAR: el proyecto original no incluía nombres de integrantes ni datos académicos,
 * por eso aquí hay marcadores de posición. Reemplázalos con los datos reales.
 */
object AppInfo {
    const val VERSION = "2.0 (Kotlin Multiplatform)"
    const val LEMA = "Avanza • Crece • Logra más"

    const val DESCRIPCION =
        "Impulsa es una aplicación de gestión de ventas y comisiones para equipos comerciales. " +
            "Permite que cada asesor registre sus ventas, consulte su avance frente a la meta, " +
            "revise su comisión estimada y compare su desempeño con el del equipo."

    const val OBJETIVO =
        "Dar a los asesores comerciales una herramienta simple para registrar ventas, " +
            "hacer seguimiento de sus metas y conocer sus comisiones, " +
            "desde el celular (Android) o desde el computador (Windows)."

    val TECNOLOGIAS = listOf(
        "Kotlin Multiplatform (KMP)",
        "Compose Multiplatform (UI compartida)",
        "Material Design 3",
        "SQLDelight + SQLite (persistencia compartida)",
        "Kotlin Coroutines y Flow",
        "Arquitectura MVVM con navegación compartida"
    )

    val PLATAFORMAS = listOf("Android (API 24+)", "Desktop (Windows, JVM)")

    // ───────── EDITAR con los datos reales ─────────
    val INTEGRANTES = listOf(
        "Daniel Arciniegas",
        "Maria Jose Peña)",
        "Katherine Tamayo"
    )

    const val ASIGNATURA = "Desarrollo de Aplicaciones Móviles"
    const val PROYECTO = "Microproyecto — Kotlin Multiplatform"
    const val INSTITUCION = "Institución / programa académico (editar en AppInfo.kt)"
    const val PERIODO = "Periodo académico (editar en AppInfo.kt)"
}
