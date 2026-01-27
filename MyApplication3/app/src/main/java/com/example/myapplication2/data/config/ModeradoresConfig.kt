package com.example.myapplication.data.config

/**
 * Configuración de moderadores del sistema Oruga
 * Aquí se definen quiénes pueden ser moderadores
 */
object ModeradorConfig {

    /**
     * OPCIÓN 1: Lista de correos predefinidos
     * Agrega aquí los emails que automáticamente serán moderadores
     */
    private val emailsModeradoresAutomaticos = listOf(
        "tu-email@duocuc.cl",                    // Tu correo
        "compañero-email@duocuc.cl",             // Correo de tu compañero
        "vpobletel@profesor.duoc.cl",            // Profesora (para demostración)
        "moderador@oruga.com"                     // Email de prueba
    )

    /**
     * OPCIÓN 2: Código secreto de moderador
     * Los usuarios pueden usar este código al registrarse
     */
    private const val CODIGO_MODERADOR_SECRETO = "ORUGA2026MOD"

    /**
     * Código de acceso alternativo (para demostración en clase)
     */
    private const val CODIGO_DEMO = "DEMO123"

    /**
     * Verifica si un email debe ser moderador automáticamente
     */
    fun esModeradorAutomatico(email: String): Boolean {
        return emailsModeradoresAutomaticos.any {
            it.equals(email, ignoreCase = true)
        }
    }

    /**
     * Verifica si un código de moderador es válido
     */
    fun validarCodigoModerador(codigo: String): Boolean {
        return codigo == CODIGO_MODERADOR_SECRETO || codigo == CODIGO_DEMO
    }

    /**
     * Obtiene el mensaje de hint para el código de moderador
     */
    fun obtenerHintCodigoModerador(): String {
        return "¿Tienes un código de moderador? (Opcional)"
    }

    /**
     * Agrega un email a la lista de moderadores (uso interno)
     * NOTA: En producción esto se haría desde un panel de admin
     */
    fun agregarModeradorManualmente(email: String) {
        // Esto es solo para debugging/testing
        // En producción usarías la base de datos
        println("📝 NOTA: Para agregar '$email' como moderador, agrégalo a la lista emailsModeradoresAutomaticos")
    }
}

/**
 * Extension function para String
 * Verifica si este email es de un moderador automático
 */
fun String.esEmailModerador(): Boolean {
    return ModeradorConfig.esModeradorAutomatico(this)
}