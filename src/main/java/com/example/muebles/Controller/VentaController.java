package com.example.muebles.Controller;
import com.example.muebles.Entity.*;
import com.example.muebles.Services.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")

public class VentaController {
    private VentaService ventaService;

    public VentaController(VentaService ventaService){
        this.ventaService = ventaService;
    }


    // POST: /api/ventas/confirmar/{id}
    @PostMapping("/confirmar/{id}")
    public ResponseEntity<?> confirmarVenta(@PathVariable Integer id) {
        try {
            Cotizacion venta = ventaService.confirmarVenta(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Venta confirmada");
            response.put("venta", venta);
            response.put("idVenta", venta.getIdCotizacion());
            response.put("total", venta.getPrecioFinal());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    //GET: /api/ventas
    @GetMapping
    public ResponseEntity<List<Cotizacion>> listarTodasVentas() {
        List<Cotizacion> ventas = ventaService.listarVentas();
        return ResponseEntity.ok(ventas);
    }

    //GET: /api/ventas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerVentaID(@PathVariable Integer id) {
        try {
            Cotizacion venta = ventaService.obtenerVentaPorId(id);
            return ResponseEntity.ok(venta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Venta inexistente: " + e.getMessage());
        }
    }

    //GET: /api/ventas/validat-stock/{id}
   @GetMapping("/validar-stock/{id}") 
   public ResponseEntity<?> validarStockPrevio(@PathVariable Integer id){
        try{
            boolean existeStock = ventaService.validarStockParaVenta(id);

            Map<String, Object> rr = new HashMap<>();
            rr.put("existeStock", rr);

            if(existeStock == true){
                rr.put("mensaje", "Stock disponible para confirmar venta");
            }else{
                rr.put("mensaje", "Stock NO disponible");
            }

            return ResponseEntity.ok(rr);
        }catch (Exception exc){
            return ResponseEntity.badRequest().body("Error!" +exc.getMessage());
        }
   }

   //GET: /api/ventas/estadisticas/total
   @GetMapping("/estadisticas/total")
    public ResponseEntity<?> obtenerTotalVentas() {
        try {
            double total = ventaService.calcularTotalVentas();
            long cantidad = ventaService.contarVentas();
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalVentas", total);
            estadisticas.put("cantidadVentas", cantidad);
            if(cantidad > 0){
                estadisticas.put("promedioVenta", total/cantidad);
            }else{
                estadisticas.put("promedioVenta", 0);
            }
            
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al calcular estadísticas: " + e.getMessage());
        }
    }

}