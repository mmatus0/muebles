package com.example.muebles.Repository;

import com.example.muebles.Entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Integer> {
    Integer countByEstadoCotizacion(String estado);
    List<Cotizacion> findByEstadoCotizacion(String estado);

}
