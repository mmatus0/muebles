package com.example.muebles.Services;

import com.example.muebles.Entity.Variante;
import com.example.muebles.Repository.VarianteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VarianteService {
    
    private VarianteRepository varianteRepository;

    public VarianteService(VarianteRepository varianteRepository){
        this.varianteRepository = varianteRepository;
    }

    // OPERACIONES DEL TIPO CRUD
    public Variante crearVariante(Variante variante) {
        return varianteRepository.save(variante);
    }

    public List<Variante> listarVariantes() {
        return varianteRepository.findAll();
    }

    public Variante listarVariante(Integer id) throws Exception {
        return varianteRepository.findById(id)
            .orElseThrow(() -> new Exception("Variante no encontrada con ID: " + id));
    }

    public Variante actualizarVariante(Integer id, Variante varianteActualizada) throws Exception {
        Variante varianteExistente = listarVariante(id);
        
        varianteExistente.setNombreVariante(varianteActualizada.getNombreVariante());
        varianteExistente.setPrecioAgregado(varianteActualizada.getPrecioAgregado());
        varianteExistente.setDescripcionVariante(varianteActualizada.getDescripcionVariante());
        
        return varianteRepository.save(varianteExistente);
    }

    public void eliminarVariante(Integer id) throws Exception {
        if (!varianteRepository.existsById(id)) {
            throw new Exception("Variante no encontrada");
        }
        varianteRepository.deleteById(id);
    }
}
