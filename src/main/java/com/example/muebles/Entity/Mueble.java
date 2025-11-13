package com.example.muebles.Entity;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "mueble")
public class Mueble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mueble", nullable = false)
    private Integer idMueble;

    @Column(name = "nombre_mueble")
    private String nombreMueble;

    @Column(name = "tipo_mueble")
    private String tipoMueble;

    @Column(name = "precio_base")
    private BigDecimal precioBase;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "estado_mueble")
    private String estadoMueble;

    @Column(name = "tamanio_mueble")
    private String tamanioMueble;

}