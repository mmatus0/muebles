package com.example.muebles.Controller;

import com.example.muebles.Entity.*;
import com.example.muebles.Services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/variantes")
@CrossOrigin(origins = "*")
public class VarianteController {

    private VarianteService varianteService;

    public VarianteController(VarianteService varianteService){
        this.varianteService = varianteService;
    }


    // POST: /api/variantes
    @PostMapping
    public ResponseEntity<?> crearNewVariante(@RequestBody Variante variante) {
        try {
            Variante nuevaVariante = varianteService.crearVariante(variante);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVariante);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


    //GET: /api/variantes
    @GetMapping
    public ResponseEntity<List<Variante>> listarTodasVarianes() {
        List<Variante> variantes = varianteService.listarVariantes();
        return ResponseEntity.ok(variantes);
    }


    //GET: /api/variantes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> listarVarianteID(@PathVariable Integer id) {
        try {
            Variante variante = varianteService.listarVariante(id);
            return ResponseEntity.ok(variante);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Variante no encontrada: " + e.getMessage());
        }
    }


    //PUT: /api/variantes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarVariante(@PathVariable Integer id, @RequestBody Variante variante) {
        try {
            Variante varianteActualizada = varianteService.actualizarVariante(id, variante);
            return ResponseEntity.ok(varianteActualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error!: " + e.getMessage());
        }
    }


    //DELETE: /pai/variantes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarVariante(@PathVariable Integer id) {
        try {
            varianteService.eliminarVariante(id);
            return ResponseEntity.ok("Variante eliminada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error!: " + e.getMessage());
        }
    }
    
}
