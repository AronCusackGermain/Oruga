package com.oruga.gaming.entity;

import com.oruga.gaming.entity.Juego;
import com.oruga.gaming.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CarritoItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    // Métodos de utilidad
    public void agregarItem(CarritoItem item) {
        items.add(item);
        item.setCarrito(this);
    }

    public void eliminarItem(CarritoItem item) {
        items.remove(item);
        item.setCarrito(null);
    }

    public Double calcularSubtotal() {
        return items.stream()
                .mapToDouble(CarritoItem::calcularSubtotal)
                .sum();
    }

    public Double calcularDescuento(boolean esModerador) {
        if (esModerador) {
            return calcularSubtotal() * 0.15; // 15% descuento
        }
        return 0.0;
    }

    public Double calcularTotal(boolean esModerador) {
        return calcularSubtotal() - calcularDescuento(esModerador);
    }

    public int getCantidadItems() {
        return items.stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }
}

