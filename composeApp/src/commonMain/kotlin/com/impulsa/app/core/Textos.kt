package com.impulsa.app.core

/**
 * Textos de la interfaz. Equivalen al antiguo strings.xml de la app Android,
 * pero en código común para que Android y Desktop compartan exactamente lo mismo.
 */
object Textos {
    const val APP_NOMBRE = "Impulsa"

    // Navegación
    const val NAV_INICIO = "Inicio"
    const val NAV_VENTAS = "Ventas"
    const val NAV_DESEMPENO = "Desempeño"
    const val NAV_COMISIONES = "Comisiones"
    const val NAV_PERFIL = "Perfil"

    // Login
    const val LOGIN_SUBTITULO = "Gestiona tus ventas y comisiones"
    const val LOGIN_CORREO = "Correo electrónico"
    const val LOGIN_PASSWORD = "Contraseña"
    const val LOGIN_ENTRAR = "Iniciar sesión"
    const val LOGIN_OLVIDE = "¿Olvidaste tu contraseña?"
    const val LOGIN_ERROR_CORREO = "Ingresa un correo válido"
    const val LOGIN_ERROR_PASS = "La contraseña debe tener al menos 6 caracteres"
    const val LOGIN_RECUPERACION = "Te enviamos un correo de recuperación"

    // Comunes
    const val VER_TODAS = "Ver todas"
    const val REGISTRAR_VENTA = "Registrar nueva venta"
    const val GUARDAR = "Guardar venta"
    const val CANCELAR = "Cancelar"
    const val VOLVER = "Volver"
    const val CERRAR_SESION = "Cerrar sesión"
    const val BUSCAR_VENTA = "Buscar cliente o concepto"
    const val ACERCA_DE = "Acerca de Impulsa"

    // Accesibilidad
    const val DESC_LOGO = "Logotipo de Impulsa"
}
