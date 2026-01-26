package com.oruga.gaming.service;

import com.oruga.gaming.dto.response.OrdenResponse;
import com.oruga.gaming.entity.CarritoItem;
import com.oruga.gaming.entity.Orden;
import com.oruga.gaming.entity.OrdenItem;
import com.oruga.gaming.exception.BadRequestException;
import com.oruga.gaming.exception.ResourceNotFoundException;
import com.oruga.gaming.repository.CarritoItemRepository;
import com.oruga.gaming.repository.CarritoRepository;
import com.oruga.gaming.repository.OrdenRepository;
import com.oruga.gaming.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final JuegoService juegoService;
    private static final double DESCUENTO_MODERADOR = 0.15;

    @Transactional
    public OrdenResponse iniciarCheckout(Long usuarioId, Boolean esModerador) {
        var carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new BadRequestException("El carrito está vacío"));

        List<CarritoItem> items = carritoItemRepository.findByCarritoId(carrito.getId());

        if (items.isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }

        // Validar stock
        for (CarritoItem item : items) {
            if (!juegoService.verificarStock(item.getJuegoId(), item.getCantidad())) {
                throw new BadRequestException("Stock insuficiente para el juego ID: " + item.getJuegoId());
            }
        }

        // Calcular totales
        BigDecimal subtotal = items.stream()
                .map(CarritoItem::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = esModerador ?
                subtotal.multiply(BigDecimal.valueOf(DESCUENTO_MODERADOR)) :
                BigDecimal.ZERO;

        BigDecimal total = subtotal.subtract(descuento);

        // Crear orden
        String numeroOrden = DateUtils.generarNumeroOrden();

        Orden orden = Orden.builder()
                .numeroOrden(numeroOrden)
                .usuarioId(usuarioId)
                .items("[]") // Se llenará con JSON de items
                .subtotal(subtotal)
                .descuento(descuento)
                .total(total)
                .estado("pendiente_pago")
                .metodoPago("transferencia")
                .build();

        orden = ordenRepository.save(orden);

        return mapToResponse(orden);
    }

    @Transactional
    public OrdenResponse subirComprobante(Long ordenId, String comprobanteUrl) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", "id", ordenId));

        orden.marcarEnRevision(comprobanteUrl);
        orden = ordenRepository.save(orden);

        return mapToResponse(orden);
    }

    public List<OrdenResponse> obtenerOrdenesPorUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrdenResponse obtenerOrdenPorId(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", "id", ordenId));
        return mapToResponse(orden);
    }

    public List<OrdenResponse> obtenerOrdenesPendientesRevision() {
        return ordenRepository.findByEstadoOrderByFechaCreacionDesc("en_revision").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrdenResponse aprobarOrden(Long ordenId, Long moderadorId, String comentario) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", "id", ordenId));

        // Reducir stock de los juegos (implementar lógica según items)
        orden.aprobar(moderadorId, comentario);
        orden = ordenRepository.save(orden);

        return mapToResponse(orden);
    }

    @Transactional
    public OrdenResponse rechazarOrden(Long ordenId, Long moderadorId, String comentario) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", "id", ordenId));

        orden.rechazar(moderadorId, comentario);
        orden = ordenRepository.save(orden);

        return mapToResponse(orden);
    }

    private OrdenResponse mapToResponse(Orden orden) {
        String estadoDescripcion = switch (orden.getEstado()) {
            case "pendiente_pago" -> "Pendiente de pago";
            case "en_revision" -> "En revisión";
            case "aprobada" -> "Aprobada";
            case "rechazada" -> "Rechazada";
            case "cancelada" -> "Cancelada";
            default -> orden.getEstado();
        };

        return OrdenResponse.builder()
                .id(orden.getId())
                .numeroOrden(orden.getNumeroOrden())
                .usuarioId(orden.getUsuarioId())
                .items(orden.getItems())
                .subtotal(orden.getSubtotal())
                .subtotalFormateado(String.format("$%,.0f", orden.getSubtotal()))
                .descuento(orden.getDescuento())
                .descuentoFormateado(String.format("$%,.0f", orden.getDescuento()))
                .total(orden.getTotal())
                .totalFormateado(orden.getTotalFormateado())
                .estado(orden.getEstado())
                .estadoDescripcion(estadoDescripcion)
                .metodoPago(orden.getMetodoPago())
                .comprobanteUrl(orden.getComprobanteUrl())
                .fechaCreacion(orden.getFechaCreacion())
                .fechaCreacionFormateada(DateUtils.formatearFechaCompleta(orden.getFechaCreacion()))
                .fechaRevision(orden.getFechaRevision())
                .comentarioModerador(orden.getComentarioModerador())
                .build();
    }
}