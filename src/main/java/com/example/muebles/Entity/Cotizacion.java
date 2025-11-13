package com.example.muebles.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "cotizacion")
public class Cotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cotizacion", nullable = false)
    private Integer idCotizacion;

    @Column(name = "fecha_cotizacion")
    private LocalDate fechaCotizacion;

    @Column(name = "estado_cotizacion")
    private String estadoCotizacion;

    @Column(name = "precio_final")
    private BigDecimal precioFinal;
}
