package com.example.muebles.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "variante")
public class Variante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_variante", nullable = false)
    private Integer idVariante;

    @Column(name = "nombre_variante")
    private String nombreVariante;
    
    @Column(name = "precio_agregado")
    private Double precioAgregado;

    @Column(name = "descripcion_variante")
    private String descripcionVariante;
}
