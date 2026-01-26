package com.oruga.gaming.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utilidades para manejo de fechas
 */
public class DateUtils {

    private static final DateTimeFormatter FORMATTER_COMPLETE = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    private static final DateTimeFormatter FORMATTER_SHORT = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private static final DateTimeFormatter FORMATTER_TIME = 
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Formatea una fecha en formato completo
     */
    public static String formatearFechaCompleta(LocalDateTime fecha) {
        if (fecha == null) return "";
        return fecha.format(FORMATTER_COMPLETE);
    }

    /**
     * Formatea una fecha en formato corto (solo día)
     */
    public static String formatearFechaCorta(LocalDateTime fecha) {
        if (fecha == null) return "";
        return fecha.format(FORMATTER_SHORT);
    }

    /**
     * Formatea solo la hora
     */
    public static String formatearHora(LocalDateTime fecha) {
        if (fecha == null) return "";
        return fecha.format(FORMATTER_TIME);
    }

    /**
     * Devuelve una representación relativa del tiempo
     * Ej: "hace 5 minutos", "hace 2 horas", "hace 3 días"
     */
    public static String formatearTiempoRelativo(LocalDateTime fecha) {
        if (fecha == null) return "";
        
        LocalDateTime ahora = LocalDateTime.now();
        long minutos = ChronoUnit.MINUTES.between(fecha, ahora);
        long horas = ChronoUnit.HOURS.between(fecha, ahora);
        long dias = ChronoUnit.DAYS.between(fecha, ahora);
        
        if (minutos < 1) {
            return "Justo ahora";
        } else if (minutos < 60) {
            return "Hace " + minutos + (minutos == 1 ? " minuto" : " minutos");
        } else if (horas < 24) {
            return "Hace " + horas + (horas == 1 ? " hora" : " horas");
        } else if (dias < 7) {
            return "Hace " + dias + (dias == 1 ? " día" : " días");
        } else if (dias < 30) {
            long semanas = dias / 7;
            return "Hace " + semanas + (semanas == 1 ? " semana" : " semanas");
        } else if (dias < 365) {
            long meses = dias / 30;
            return "Hace " + meses + (meses == 1 ? " mes" : " meses");
        } else {
            long años = dias / 365;
            return "Hace " + años + (años == 1 ? " año" : " años");
        }
    }

    /**
     * Genera un código de orden único basado en fecha
     * Formato: ORD-2026-XXXXX
     */
    public static String generarNumeroOrden() {
        LocalDateTime ahora = LocalDateTime.now();
        int año = ahora.getYear();
        long timestamp = System.currentTimeMillis() % 100000; // Últimos 5 dígitos
        return String.format("ORD-%d-%05d", año, timestamp);
    }

    private DateUtils() {
        // Constructor privado para evitar instanciación
    }
}
