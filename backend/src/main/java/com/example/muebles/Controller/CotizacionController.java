package com.example.muebles.Controller;
import com.example.muebles.Entity.*;
import com.example.muebles.Services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/cotizaciones")
@CrossOrigin(origins = "*")
public class CotizacionController {

    @Autowired
    private CotizacionService cotizacionService;
    
    // POST: /api/cotizaciones
    @PostMapping
    public ResponseEntity<?> crearCotizacion() {
        try {
            Cotizacion cotizacion = cotizacionService.crearCotizacion();
            return ResponseEntity.status(HttpStatus.CREATED).body(cotizacion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear cotización: " + e.getMessage());
        }
    }

    /**
     * POST: /api/cotizaciones/{id}/detalles
     * Body: { "idMueble": 1, "idVariante": 2, "cantidad": 3 }
     */
    @PostMapping("/{id}/detalles")
    public ResponseEntity<?> agregarDetalle(@PathVariable Integer id, @RequestBody Map <String, Object> detalle) {
        try {
            Integer idMueble = Integer.valueOf(detalle.get("idMueble").toString());
            Integer idVariante = detalle.get("idVariante") != null ? 
                Integer.valueOf(detalle.get("idVariante").toString()) : null;
            int cantidad = Integer.parseInt(detalle.get("cantidad").toString());
            
            DetalleCotizacion nuevoDetalle = cotizacionService.agregarDetalle(id, idMueble, idVariante, cantidad);
            
            
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDetalle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al agregar detalle: " + e.getMessage());
        } 
    }

    //GET: /api/cotizaciones
    @GetMapping
    public ResponseEntity<List<Cotizacion>> listarCotizaciones() {
        List<Cotizacion> cotizacionesListado = cotizacionService.listarTodasCotizaciones();
        return ResponseEntity.ok(cotizacionesListado);
    }

    //GET: /api/cotizaciones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> listarCotizacion(@PathVariable Integer id) {
        try {
            Cotizacion cotizacion = cotizacionService.listarCotizacion(id);
            return ResponseEntity.ok(cotizacion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Cotización no encontrada: " + e.getMessage());
        }
    }

    //PUT: /api/cotizaciones/{id}/cancelar
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarCotizacion(@PathVariable Integer id) {
        try {
            cotizacionService.cancelarCotizacion(id);
            return ResponseEntity.ok("Cotización cancelada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al cancelar cotización: " + e.getMessage());
        }
    }

    //DELETE: /api/cotizaciones/detalles/{idDetalle}
    @DeleteMapping("/detalles/{idDetalle}")
    public ResponseEntity<?> eliminarDetalle(@PathVariable Integer idDetalle) {
        try {
            cotizacionService.eliminarDetalle(idDetalle);
            return ResponseEntity.ok("Detalle eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar detalle: " + e.getMessage());
        }
    }
}