package com.oruga.gaming.service;

import com.oruga.gaming.exception.BadRequestException;
import com.oruga.gaming.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

/**
 * Servicio para almacenamiento de archivos (imágenes, comprobantes)
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${upload.path:uploads/}")
    private String uploadPath;

    /**
     * Guarda una imagen de publicación
     */
    public String guardarImagenPublicacion(MultipartFile file) {
        return guardarArchivo(file, Constants.UPLOAD_DIR_PUBLICACIONES, true);
    }

    /**
     * Guarda una imagen de mensaje
     */
    public String guardarImagenMensaje(MultipartFile file) {
        return guardarArchivo(file, Constants.UPLOAD_DIR_MENSAJES, true);
    }

    /**
     * Guarda un comprobante de pago
     */
    public String guardarComprobantePago(MultipartFile file) {
        return guardarArchivo(file, Constants.UPLOAD_DIR_COMPROBANTES, false);
    }

    /**
     * Guarda un archivo en el sistema
     */
    private String guardarArchivo(MultipartFile file, String subdirectorio, boolean soloImagenes) {
        try {
            // Validar archivo
            if (file.isEmpty()) {
                throw new BadRequestException("El archivo está vacío");
            }

            // Validar tamaño
            if (file.getSize() > Constants.MAX_FILE_SIZE) {
                throw new BadRequestException("El archivo excede el tamaño máximo permitido (5MB)");
            }

            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null) {
                throw new BadRequestException("Tipo de archivo no válido");
            }

            if (soloImagenes) {
                // Validar que sea imagen
                boolean esImagenValida = Arrays.asList(Constants.ALLOWED_IMAGE_TYPES)
                        .contains(contentType);
                if (!esImagenValida) {
                    throw new BadRequestException("Solo se permiten imágenes (JPG, PNG, GIF)");
                }
            } else {
                // Validar que sea imagen o PDF
                boolean esArchivoValido = Arrays.asList(Constants.ALLOWED_IMAGE_TYPES)
                        .contains(contentType) ||
                        Arrays.asList(Constants.ALLOWED_DOCUMENT_TYPES).contains(contentType);
                if (!esArchivoValido) {
                    throw new BadRequestException("Solo se permiten imágenes o PDFs");
                }
            }

            // Limpiar nombre de archivo
            String nombreOriginal = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));

            // Generar nombre único
            String nombreArchivo = UUID.randomUUID().toString() + extension;

            // Crear directorio si no existe
            Path directorioPath = Paths.get(uploadPath + subdirectorio);
            if (!Files.exists(directorioPath)) {
                Files.createDirectories(directorioPath);
            }

            // Guardar archivo
            Path archivoPath = directorioPath.resolve(nombreArchivo);
            Files.copy(file.getInputStream(), archivoPath, StandardCopyOption.REPLACE_EXISTING);

            // Retornar ruta relativa
            String rutaRelativa = subdirectorio + nombreArchivo;
            log.info("Archivo guardado: {}", rutaRelativa);

            return rutaRelativa;

        } catch (IOException ex) {
            log.error("Error al guardar archivo: {}", ex.getMessage());
            throw new BadRequestException("Error al guardar el archivo: " + ex.getMessage());
        }
    }

    /**
     * Elimina un archivo
     */
    public void eliminarArchivo(String rutaArchivo) {
        try {
            Path archivoPath = Paths.get(uploadPath + rutaArchivo);
            Files.deleteIfExists(archivoPath);
            log.info("Archivo eliminado: {}", rutaArchivo);
        } catch (IOException ex) {
            log.error("Error al eliminar archivo: {}", ex.getMessage());
        }
    }

    /**
     * Verifica si un archivo existe
     */
    public boolean existeArchivo(String rutaArchivo) {
        Path archivoPath = Paths.get(uploadPath + rutaArchivo);
        return Files.exists(archivoPath);
    }

    /**
     * Obtiene la ruta completa de un archivo
     */
    public Path obtenerRutaCompleta(String rutaArchivo) {
        return Paths.get(uploadPath + rutaArchivo);
    }
}