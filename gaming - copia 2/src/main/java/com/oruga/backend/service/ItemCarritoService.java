package com.oruga.backend.service;

import com.oruga.backend.dto.request.ActualizarCantidadRequest;
import com.oruga.backend.dto.request.AgregarCarritoRequest;
import com.oruga.backend.dto.response.CompraResponse;
import com.oruga.backend.dto.response.ItemCarritoResponse;
import com.oruga.backend.model.ItemCarrito;
import com.oruga.backend.model.Usuario;
import com.oruga.backend.repository.ItemCarritoRepository;
import com.oruga.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar Items del Carrito de Compras
 */
@Service
@RequiredArgsConstructor
public class ItemCarritoService {

    private final ItemCarritoRepository itemCarritoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Agregar item al carrito
     */
    @Transactional
    public ItemCarritoResponse agregarAlCarrito(AgregarCarritoRequest request, Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);

        // Verificar si el juego ya está en el carrito
        var itemExistente = itemCarritoRepository.findByUsuarioAndJuegoId(usuario, Long.valueOf(request.getJuegoId()));

        ItemCarrito item;
        if (itemExistente.isPresent()) {
            // Actualizar cantidad
            item = itemExistente.get();
            item.setCantidad(item.getCantidad() + request.getCantidad());
        } else {
            // Crear nuevo item
            item = ItemCarrito.builder()
                    .usuario(usuario)
                    .juegoId(request.getJuegoId())
                    .nombreJuego(request.getNombreJuego())
                    .precioUnitario(request.getPrecioUnitario())
                    .cantidad(request.getCantidad())
                    .imagenUrl(request.getImagenUrl())
                    .build();
        }

        itemCarritoRepository.save(item);
        return mapearAResponse(item);
    }

    /**
     * Obtener todos los items del carrito del usuario
     */
    public List<ItemCarritoResponse> obtenerCarrito(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        return itemCarritoRepository.findByUsuarioOrderByFechaAgregadoDesc(usuario)
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar cantidad de un item
     */
    @Transactional
    public ItemCarritoResponse actualizarCantidad(Long itemId, ActualizarCantidadRequest request, Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (!item.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para modificar este item");
        }

        item.setCantidad(request.getCantidad());
        itemCarritoRepository.save(item);
        return mapearAResponse(item);
    }

    /**
     * Eliminar un item del carrito
     */
    @Transactional
    public void eliminarItem(Long itemId, Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (!item.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este item");
        }

        itemCarritoRepository.delete(item);
    }

    /**
     * Vaciar todo el carrito
     */
    @Transactional
    public void vaciarCarrito(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        itemCarritoRepository.deleteAllByUsuario(usuario);
    }

    /**
     * Contar items en el carrito
     */
    public Long contarItems(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        return itemCarritoRepository.countByUsuario(usuario);
    }

    /**
     * Calcular total del carrito
     */
    public Double calcularTotal(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        List<ItemCarrito> items = itemCarritoRepository.findByUsuarioOrderByFechaAgregadoDesc(usuario);
        return items.stream()
                .mapToDouble(ItemCarrito::calcularSubtotal)
                .sum();
    }

    /**
     * Procesar compra (simular)
     */
    @Transactional
    public CompraResponse procesarCompra(Authentication auth) {
        Usuario usuario = obtenerUsuarioAutenticado(auth);
        List<ItemCarrito> items = itemCarritoRepository.findByUsuarioOrderByFechaAgregadoDesc(usuario);

        if (items.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Double total = items.stream()
                .mapToDouble(ItemCarrito::calcularSubtotal)
                .sum();

        int cantidadItems = items.size();

        // Vaciar carrito después de "compra"
        itemCarritoRepository.deleteAllByUsuario(usuario);

        return CompraResponse.builder()
                .mensaje("¡Compra realizada con éxito!")
                .total(total)
                .cantidadItems(cantidadItems)
                .build();
    }

    // ========== MÉTODOS AUXILIARES ==========

    private Usuario obtenerUsuarioAutenticado(Authentication auth) {
        return usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private ItemCarritoResponse mapearAResponse(ItemCarrito item) {
        return ItemCarritoResponse.builder()
                .id(item.getId())
                .juegoId(Long.valueOf(item.getJuegoId()))
                .nombreJuego(item.getNombreJuego())
                .precioUnitario(item.getPrecioUnitario())
                .cantidad(item.getCantidad())
                .imagenUrl(String.valueOf(item.getImagenUrl()))
                .subtotal(item.calcularSubtotal())
                .fechaAgregado(item.getFechaAgregado())
                .build();
    }
}