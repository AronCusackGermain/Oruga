package com.oruga.gaming.service;

import com.oruga.gaming.dto.CarritoItemDto;
import com.oruga.gaming.dto.CarritoResponseDto;
import com.oruga.gaming.entity.*;
import com.oruga.gaming.repository.Carritorepository;
import com.oruga.gaming.repository.Juegorepository;
import com.oruga.gaming.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarritoService {

    private final Carritorepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final Juegorepository juegoRepository;
    private final JuegoService juegoService;

    public CarritoService(Carritorepository carritoRepository,
                         UsuarioRepository usuarioRepository,
                         Juegorepository juegoRepository,
                         JuegoService juegoService) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.juegoRepository = juegoRepository;
        this.juegoService = juegoService;
    }

    public CarritoResponseDto obtenerCarritoPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> crearCarritoParaUsuario(usuario));

        return toDto(carrito, usuario.getEsModerador());
    }

    public CarritoResponseDto agregarItem(String email, Long juegoId, Integer cantidad) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Juego juego = juegoRepository.findById(juegoId)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));

        if (!juego.tieneStock()) {
            throw new RuntimeException("Juego sin stock");
        }

        if (juego.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente");
        }

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> crearCarritoParaUsuario(usuario));

        // Buscar si el item ya existe
        CarritoItem itemExistente = carrito.getItems().stream()
                .filter(item -> item.getJuego().getId().equals(juegoId))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            // Incrementar cantidad
            itemExistente.incrementarCantidad(cantidad);
        } else {
            // Crear nuevo item
            CarritoItem nuevoItem = CarritoItem.builder()
                    .carrito(carrito)
                    .juego(juego)
                    .cantidad(cantidad)
                    .precioUnitario(juego.getPrecio())
                    .build();
            carrito.agregarItem(nuevoItem);
        }

        Carrito carritoGuardado = carritoRepository.save(carrito);
        return toDto(carritoGuardado, usuario.getEsModerador());
    }

    public void eliminarItem(String email, Long juegoId) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        CarritoItem item = carrito.getItems().stream()
                .filter(i -> i.getJuego().getId().equals(juegoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item no encontrado en carrito"));

        carrito.eliminarItem(item);
        carritoRepository.save(carrito);
    }

    public void vaciarCarrito(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    public String procesarCompra(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Validar stock de todos los items
        for (CarritoItem item : carrito.getItems()) {
            Juego juego = item.getJuego();
            if (juego.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + juego.getNombre());
            }
        }

        // Actualizar stock de los juegos
        for (CarritoItem item : carrito.getItems()) {
            juegoService.actualizarStock(item.getJuego().getId(), item.getCantidad());
        }

        Double total = carrito.calcularTotal(usuario.getEsModerador());
        
        // Vaciar carrito después de la compra
        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return String.format("Compra procesada exitosamente. Total: $%,.0f", total)
                .replace(",", ".");
    }

    private Carrito crearCarritoParaUsuario(Usuario usuario) {
        Carrito carrito = Carrito.builder()
                .usuario(usuario)
                .items(new ArrayList<>())
                .build();
        return carritoRepository.save(carrito);
    }

    private CarritoResponseDto toDto(Carrito carrito, Boolean esModerador) {
        List<CarritoItemDto> itemsDto = carrito.getItems().stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());

        Double subtotal = carrito.calcularSubtotal();
        Double descuento = carrito.calcularDescuento(esModerador);
        Double total = carrito.calcularTotal(esModerador);

        return CarritoResponseDto.builder()
                .id(carrito.getId())
                .items(itemsDto)
                .cantidadItems(carrito.getCantidadItems())
                .subtotal(subtotal)
                .subtotalFormateado(formatearPrecio(subtotal))
                .descuento(descuento)
                .descuentoFormateado(formatearPrecio(descuento))
                .total(total)
                .totalFormateado(formatearPrecio(total))
                .tieneDescuentoModerador(esModerador)
                .build();
    }

    private CarritoItemDto toItemDto(CarritoItem item) {
        return CarritoItemDto.builder()
                .id(item.getId())
                .juegoId(item.getJuego().getId())
                .nombre(item.getJuego().getNombre())
                .precioUnitario(item.getPrecioUnitario())
                .cantidad(item.getCantidad())
                .subtotal(item.calcularSubtotal())
                .stockDisponible(item.getJuego().getStock())
                .build();
    }

    private String formatearPrecio(Double precio) {
        return String.format("$%,.0f", precio).replace(",", ".");
    }
}
