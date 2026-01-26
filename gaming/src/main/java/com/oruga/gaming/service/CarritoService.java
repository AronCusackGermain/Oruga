package com.oruga.gaming.service;

import com.oruga.gaming.dto.request.AgregarCarritoRequest;
import com.oruga.gaming.dto.response.CarritoResponse;
import com.oruga.gaming.entity.Carrito;
import com.oruga.gaming.entity.CarritoItem;
import com.oruga.gaming.entity.Juego;
import com.oruga.gaming.exception.BadRequestException;
import com.oruga.gaming.exception.ResourceNotFoundException;
import com.oruga.gaming.repository.CarritoItemRepository;
import com.oruga.gaming.repository.CarritoRepository;
import com.oruga.gaming.repository.JuegoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final JuegoRepository juegoRepository;
    private static final double DESCUENTO_MODERADOR = 0.15; // 15%

    @Transactional
    public CarritoResponse agregarAlCarrito(Long usuarioId, Boolean esModerador,
                                            AgregarCarritoRequest request) {
        // Obtener o crear carrito
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = Carrito.builder()
                            .usuarioId(usuarioId)
                            .build();
                    return carritoRepository.save(nuevo);
                });

        // Verificar juego
        Juego juego = juegoRepository.findById(request.getJuegoId())
                .orElseThrow(() -> new ResourceNotFoundException("Juego", "id", request.getJuegoId()));

        // Verificar stock
        if (juego.getStock() < request.getCantidad()) {
            throw new BadRequestException("Stock insuficiente. Disponible: " + juego.getStock());
        }

        // Buscar si ya existe en el carrito
        CarritoItem itemExistente = carritoItemRepository
                .findByCarritoIdAndJuegoId(carrito.getId(), juego.getId())
                .orElse(null);

        if (itemExistente != null) {
            // Actualizar cantidad
            itemExistente.incrementarCantidad(request.getCantidad());
            carritoItemRepository.save(itemExistente);
        } else {
            // Crear nuevo item
            CarritoItem nuevoItem = CarritoItem.builder()
                    .carritoId(carrito.getId())
                    .juegoId(juego.getId())
                    .cantidad(request.getCantidad())
                    .precioUnitario(juego.getPrecio())
                    .build();
            carritoItemRepository.save(nuevoItem);
        }

        return obtenerCarrito(usuarioId, esModerador);
    }

    public CarritoResponse obtenerCarrito(Long usuarioId, Boolean esModerador) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        List<CarritoItem> items = carritoItemRepository.findByCarritoId(carrito.getId());

        // Calcular totales
        BigDecimal subtotal = items.stream()
                .map(CarritoItem::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = esModerador ?
                subtotal.multiply(BigDecimal.valueOf(DESCUENTO_MODERADOR)) :
                BigDecimal.ZERO;

        BigDecimal total = subtotal.subtract(descuento);

        // Mapear items
        List<CarritoResponse.CarritoItemResponse> itemsResponse = items.stream()
                .map(item -> {
                    Juego juego = juegoRepository.findById(item.getJuegoId()).orElse(null);
                    if (juego == null) return null;

                    return CarritoResponse.CarritoItemResponse.builder()
                            .id(item.getId())
                            .juegoId(juego.getId())
                            .nombre(juego.getNombre())
                            .descripcion(juego.getDescripcion())
                            .imagenUrl(juego.getImagenUrl())
                            .precioUnitario(item.getPrecioUnitario())
                            .precioUnitarioFormateado(String.format("$%,.0f", item.getPrecioUnitario()))
                            .cantidad(item.getCantidad())
                            .stockDisponible(juego.getStock())
                            .subtotal(item.calcularSubtotal())
                            .subtotalFormateado(String.format("$%,.0f", item.calcularSubtotal()))
                            .build();
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());

        return CarritoResponse.builder()
                .id(carrito.getId())
                .items(itemsResponse)
                .cantidadItems(items.size())
                .subtotal(subtotal)
                .subtotalFormateado(String.format("$%,.0f", subtotal))
                .descuento(descuento)
                .descuentoFormateado(String.format("$%,.0f", descuento))
                .total(total)
                .totalFormateado(String.format("$%,.0f", total))
                .tieneDescuentoModerador(esModerador)
                .build();
    }

    @Transactional
    public void actualizarCantidad(Long usuarioId, Long juegoId, Integer cantidad) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        CarritoItem item = carritoItemRepository.findByCarritoIdAndJuegoId(carrito.getId(), juegoId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado en el carrito"));

        item.actualizarCantidad(cantidad);
        carritoItemRepository.save(item);
    }

    @Transactional
    public void eliminarItem(Long usuarioId, Long juegoId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        CarritoItem item = carritoItemRepository.findByCarritoIdAndJuegoId(carrito.getId(), juegoId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado en el carrito"));

        carritoItemRepository.delete(item);
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        carritoItemRepository.deleteByCarritoId(carrito.getId());
    }
}