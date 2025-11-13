package com.example.muebles.Controller;
import com.example.muebles.Entity.*;
import com.example.muebles.Services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/precios")
@CrossOrigin(origins = "*")


public class PrecioController {
    private PrecioService precioService;
    private MuebleService muebleService;
    private VarianteService varianteService;

    public PrecioController(PrecioService precioService, MuebleService muebleService, VarianteService varianteService){
        this.muebleService = muebleService;
        this.precioService = precioService;
        this.varianteService = varianteService;
    }

    // Aquí se calcula el precio con todo (variantes y cantidades)
    //GET: /api/precios/calcular
    @GetMapping("/calcular")
    public ResponseEntity<?> calcularPrecio(@RequestParam Integer idMueble, @RequestParam Integer idVariante, @RequestParam int cantidad) {
        try {

            Mueble mueble = muebleService.listarMueble(idMueble);
            Variante variante = null;
            
            if (idVariante != null) {
                variante = varianteService.listarVariante(idVariante);
            }
            
            double precioUnitario = precioService.calcularPrecioUnitario(mueble, variante);
            double subtotal = precioService.calcularSubtotal(mueble, variante, cantidad);
            double precioAgregado = precioService.obtenerPrecioAgregado(variante);
            double porcentajeIncremento = precioService.calcularPorcentajeIncremento(mueble, variante);
            
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("mueble", mueble.getNombreMueble());
            resultado.put("precioBase", mueble.getPrecioBase());
            resultado.put("variante", variante != null ? variante.getNombreVariante() : "Normal");
            resultado.put("precioAgregado", precioAgregado);
            resultado.put("precioUnitario", precioUnitario);
            resultado.put("cantidad", cantidad);
            resultado.put("subtotal", subtotal);
            resultado.put("porcentajeIncremento", porcentajeIncremento);
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al calcular precio: " + e.getMessage());
        }
    }

    // Aquí se calcula el precio de forma unitario
    //GET: /api/precios/unitario
    @GetMapping("/unitario")
    public ResponseEntity<?> calcularPrecioUnitario(@RequestParam Integer idMueble, @RequestParam Integer idVariante) {
        try {
            Mueble mueble = muebleService.listarMueble(idMueble);
            Variante variante = null;
            
            if (idVariante != null) {
                variante = varianteService.listarVariante(idVariante);
            }
            
            double precioUnitario = precioService.calcularPrecioUnitario(mueble, variante);
            
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("precioUnitario", precioUnitario);
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al calcular precio unitario: " + e.getMessage());
        }
    }

    //GET: /api/precios/incremento
    @GetMapping("/incremento")
    public ResponseEntity<?> calcularPorcentajeIncremento(@RequestParam Integer idMueble, @RequestParam Integer idVariante) {
        try {
            Mueble mueble = muebleService.listarMueble(idMueble);
            Variante variante = varianteService.listarVariante(idVariante);
            
            double porcentaje = precioService.calcularPorcentajeIncremento(mueble, variante);
            
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("mueble", mueble.getNombreMueble());
            resultado.put("precioBase", mueble.getPrecioBase());
            resultado.put("variante", variante.getNombreVariante());
            resultado.put("precioAgregado", variante.getPrecioAgregado());
            resultado.put("porcentajeIncremento", porcentaje);
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al calcular incremento: " + e.getMessage());
        }
    }
}