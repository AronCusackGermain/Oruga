package com.oruga.backend.util;

/**
 * Clase de constantes de la aplicación
 */
public class Constants {

    // JWT
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    
    // Roles
    public static final String ROLE_USER = "USUARIO";
    public static final String ROLE_MODERATOR = "MODERADOR";
    
    // Estados de orden
    public static final String ORDEN_PENDIENTE_PAGO = "pendiente_pago";
    public static final String ORDEN_EN_REVISION = "en_revision";
    public static final String ORDEN_APROBADA = "aprobada";
    public static final String ORDEN_RECHAZADA = "rechazada";
    public static final String ORDEN_CANCELADA = "cancelada";
    
    // Tipos de mensaje
    public static final String TIPO_MENSAJE_TEXTO = "texto";
    public static final String TIPO_MENSAJE_IMAGEN = "imagen";
    public static final String TIPO_MENSAJE_TEXTO_CON_IMAGEN = "texto_con_imagen";
    
    // Formatos
    public static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMATO_FECHA_CORTA = "dd/MM/yyyy";
    
    // Paginación
    public static final int PAGE_SIZE_DEFAULT = 20;
    public static final int PAGE_SIZE_MAX = 100;
    
    // Validaciones
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 50;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    
    // Upload
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif"};
    public static final String[] ALLOWED_DOCUMENT_TYPES = {"application/pdf"};
    
    // Directorios de upload
    public static final String UPLOAD_DIR_PUBLICACIONES = "uploads/publicaciones/";
    public static final String UPLOAD_DIR_MENSAJES = "uploads/mensajes/";
    public static final String UPLOAD_DIR_COMPROBANTES = "uploads/comprobantes/";
    
    // Descuentos
    public static final double DESCUENTO_MODERADOR = 0.15; // 15%
    
    private Constants() {
        // Constructor privado para evitar instanciación
    }
}
