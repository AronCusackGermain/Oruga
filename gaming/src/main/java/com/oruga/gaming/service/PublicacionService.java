package com.oruga.gaming.service;

import com.oruga.gaming.dto.PublicacionDto;
import com.oruga.gaming.entity.Publicacion;
import com.oruga.gaming.entity.Usuario;
import com.oruga.gaming.repository.PublicacionRepository;
import com.oruga.gaming.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;

    public PublicacionService(PublicacionRepository publicacionRepository,
                              UsuarioRepository usuarioRepository) {
        this.publicacionRepository = publicacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Crear una nueva publicación
     */
    public PublicacionDto crearPublicacion(String email, String titulo,
                                           String contenido, Boolean esAnuncio) {
        Usuario autor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (titulo.isBlank() || contenido.isBlank()) {
            throw new RuntimeException("El título y contenido son obligatorios");
        }

        // Solo moderadores pueden crear anuncios
        Boolean anuncio = esAnuncio != null && esAnuncio && autor.getEsModerador();

        Publicacion publicacion = Publicacion.builder()
                .autor(autor)
                .titulo(titulo)
                .contenido(contenido)
                .likes(0)
                .cantidadComentarios(0)
                .esAnuncio(anuncio)
                .build();

        Publicacion publicacionGuardada = publicacionRepository.save(publicacion);

        // Incrementar contador de publicaciones del usuario
        usuarioRepository.findById(autor.getId()).ifPresent(u -> {
            u.setCantidadPublicaciones(u.getCantidadPublicaciones() + 1);
            usuarioRepository.save(u);
        });

        return toDto(publicacionGuardada);
    }

    /**
     * Obtener todas las publicaciones
     */
    public List<PublicacionDto> obtenerTodasPublicaciones() {
        return publicacionRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener solo anuncios
     */
    public List<PublicacionDto> obtenerAnuncios() {
        return publicacionRepository.findByEsAnuncioTrueOrderByFechaCreacionDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener publicaciones por autor
     */
    public List<PublicacionDto> obtenerPublicacionesPorAutor(Long autorId) {
        return publicacionRepository.findByAutorIdOrderByFechaCreacionDesc(autorId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener publicación por ID
     */
    public PublicacionDto obtenerPorId(Long id) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));
        return toDto(publicacion);
    }

    /**
     * Dar like a una publicación
     */
    public void darLike(Long publicacionId) {
        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        publicacion.setLikes(publicacion.getLikes() + 1);
        publicacionRepository.save(publicacion);
    }

    /**
     * Incrementar contador de comentarios
     */
    public void incrementarComentarios(Long publicacionId) {
        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        publicacion.setCantidadComentarios(publicacion.getCantidadComentarios() + 1);
        publicacionRepository.save(publicacion);
    }

    /**
     * Eliminar publicación
     */
    public void eliminarPublicacion(String email, Long publicacionId) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        // Solo el autor o un moderador pueden eliminar
        if (!publicacion.getAutor().getId().equals(usuario.getId())
                && !usuario.getEsModerador()) {
            throw new RuntimeException("No tienes permiso para eliminar esta publicación");
        }

        publicacionRepository.delete(publicacion);

        // Decrementar contador del autor
        Usuario autor = publicacion.getAutor();
        if (autor.getCantidadPublicaciones() > 0) {
            autor.setCantidadPublicaciones(autor.getCantidadPublicaciones() - 1);
            usuarioRepository.save(autor);
        }
    }

    /**
     * Actualizar publicación
     */
    public PublicacionDto actualizarPublicacion(String email, Long publicacionId,
                                                String titulo, String contenido) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        // Solo el autor puede editar
        if (!publicacion.getAutor().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para editar esta publicación");
        }

        if (titulo != null && !titulo.isBlank()) {
            publicacion.setTitulo(titulo);
        }
        if (contenido != null && !contenido.isBlank()) {
            publicacion.setContenido(contenido);
        }

        Publicacion publicacionActualizada = publicacionRepository.save(publicacion);
        return toDto(publicacionActualizada);
    }

    /**
     * Convertir entidad a DTO
     */
    public PublicacionDto toDto(Publicacion publicacion) {
        return PublicacionDto.builder()
                .id(publicacion.getId())
                .titulo(publicacion.getTitulo())
                .contenido(publicacion.getContenido())
                .autorId(publicacion.getAutor().getId())
                .autorNombre(publicacion.getAutor().getNombreUsuario())
                .likes(publicacion.getLikes())
                .cantidadComentarios(publicacion.getCantidadComentarios())
                .fechaCreacion(publicacion.getFechaCreacion().toString())
                .tiempoRelativo(calcularTiempoRelativo(publicacion.getFechaCreacion()))
                .esAnuncio(publicacion.getEsAnuncio())
                .build();
    }

    /**
     * Calcular tiempo relativo
     */
    private String calcularTiempoRelativo(LocalDateTime fechaCreacion) {
        Duration duration = Duration.between(fechaCreacion, LocalDateTime.now());
        long minutos = duration.toMinutes();

        if (minutos < 1) return "Hace un momento";
        if (minutos < 60) return "Hace " + minutos + " minuto" + (minutos > 1 ? "s" : "");

        long horas = duration.toHours();
        if (horas < 24) return "Hace " + horas + " hora" + (horas > 1 ? "s" : "");

        long dias = duration.toDays();
        if (dias < 7) return "Hace " + dias + " día" + (dias > 1 ? "s" : "");

        long semanas = dias / 7;
        if (semanas < 4) return "Hace " + semanas + " semana" + (semanas > 1 ? "s" : "");

        long meses = dias / 30;
        return "Hace " + meses + " mes" + (meses > 1 ? "es" : "");
    }
}