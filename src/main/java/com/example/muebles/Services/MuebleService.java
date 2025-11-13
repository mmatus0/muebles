package com.example.muebles.Services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.muebles.Entity.Mueble;
import com.example.muebles.Repository.MuebleRepository;

@Service
public class MuebleService {

    private MuebleRepository muebleRepository;

    public MuebleService(MuebleRepository muebleRepository){
        this.muebleRepository = muebleRepository;
    }

    //MÉTODOS CON OPERACIONES DEL TIPO CRUD

    public Mueble crearMueble(Mueble mueble){
        if(mueble.getStock() > 0){
            mueble.setEstadoMueble("activo");
        }
        return muebleRepository.save(mueble);
    }

    public List<Mueble> listarMuebles(){
        return muebleRepository.findAll();
    }

    public Mueble listarMueble(Integer id) throws Exception{
        return muebleRepository.findById(id).orElseThrow(() -> new Exception ("Mueble no encontrado"));
    }

    public Mueble actualizarMueble(Integer id, Mueble muebleActualizado) throws Exception {
        
         Mueble muebleExistente = listarMueble(id);
        
        muebleExistente.setNombreMueble(muebleActualizado.getNombreMueble());
        muebleExistente.setTipoMueble(muebleActualizado.getTipoMueble());
        muebleExistente.setPrecioBase(muebleActualizado.getPrecioBase());
        muebleExistente.setStock(muebleActualizado.getStock());
        muebleExistente.setTamanioMueble(muebleActualizado.getTamanioMueble());
        muebleExistente.setMaterialMueble(muebleActualizado.getMaterialMueble());
        
        return muebleRepository.save(muebleExistente);
    }

   public void eliminarMueble(Integer id) throws Exception {
    if (!muebleRepository.existsById(id)) {
        throw new Exception("Mueble no encontrado");
    }
    muebleRepository.deleteById(id);
}

    //MÉTODOS RELACIONADOS CON EL ESTADO DEL MUEBLE

    public void desactivarMueble(Integer id) throws Exception {
        Mueble mueble = listarMueble(id);
        mueble.setEstadoMueble("inactivo");
        muebleRepository.save(mueble);
    }

    public void activarMueble(Integer id) throws Exception {
        Mueble mueble = listarMueble(id);
        mueble.setEstadoMueble("activo");
        muebleRepository.save(mueble);
    }

    // MÉTODO RELACIONADOS CON STOCK DE MUEBLE (usados por VentaService)

     public boolean verificarStock(Integer idMueble, int cantidad) throws Exception {
        Mueble mueble = listarMueble(idMueble);
        return mueble.getStock() >= cantidad;
    }

    public void disminuirStock(Integer idMueble, int cantidad) throws Exception {
        Mueble mueble = listarMueble(idMueble);
        
        if (mueble.getStock() < cantidad) {
            throw new Exception("Stock insuficiente para: " + mueble.getNombreMueble());
        }
        
        mueble.setStock(mueble.getStock() - cantidad);
        muebleRepository.save(mueble);
    }

     public void aumentarStock(Integer idMueble, int cantidad) throws Exception {
        Mueble mueble = listarMueble(idMueble);
        mueble.setStock(mueble.getStock() + cantidad);
        muebleRepository.save(mueble);
    }    
}
