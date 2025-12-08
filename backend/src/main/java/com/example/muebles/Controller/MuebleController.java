package com.example.muebles.Controller;
import com.example.muebles.Entity.Mueble;
import com.example.muebles.Services.MuebleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/muebles")
@CrossOrigin(origins = "*")

public class MuebleController {
    private MuebleService muebleService;

    public MuebleController(MuebleService muebleService){
        this.muebleService = muebleService;
    }

    //POST: /api/muebles
    @PostMapping
    public ResponseEntity<?> crearMueble(@RequestBody Mueble mueble) {
        try {
            Mueble nuevoMueble = muebleService.crearMueble(mueble);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMueble);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear mueble: " + e.getMessage());
        }
    }

    //GET: /api/muebles
    @GetMapping
    public ResponseEntity<List<Mueble>> listarTodosMuebles() {
        List<Mueble> muebles = muebleService.listarMuebles();
        return ResponseEntity.ok(muebles);
    }

    //GET: /api/muebles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
        try {
            Mueble mueble = muebleService.listarMueble(id);
            return ResponseEntity.ok(mueble);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mueble no encontrado: " + e.getMessage());
        }
    }

    //PUT: /api/muebles/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMueble(@PathVariable Integer id, @RequestBody Mueble mueble) {
        try {
            Mueble muebleActualizado = muebleService.actualizarMueble(id, mueble);
            return ResponseEntity.ok(muebleActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar mueble: " + e.getMessage());
        }
    }

    //DELETE: /api/muebles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarMueble(@PathVariable Integer id) {
        try {
            muebleService.desactivarMueble(id);
            return ResponseEntity.ok("Mueble desactivado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al desactivar mueble: " + e.getMessage());
        }
    }

    //GET: /api/muebles/{idMueble}/stock/{cantidad}
    @GetMapping("/{id}/stock/{cantidad}")
    public ResponseEntity<?> verificarStock(@PathVariable Integer id, @PathVariable int cantidad) {
        try {
            boolean hayStock = muebleService.verificarStock(id, cantidad);
            if (hayStock) {
                return ResponseEntity.ok("Stock disponible");
            } else {
                return ResponseEntity.ok("Stock insuficiente");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al verificar stock: " + e.getMessage());
        }
    }
}
