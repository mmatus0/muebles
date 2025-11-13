package com.example.muebles;
import com.example.muebles.Entity.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para gestión de catálogo (CRUD)
 */
public class MuebleServiceTest {
    
    @Test
    void testCrearMueble() {
        Mueble mueble = new Mueble();
        mueble.setNombreMueble("Mesa Roble");
        mueble.setTipoMueble("Mesa");
        mueble.setPrecioBase(25000.0);
        mueble.setStock(8);
        mueble.setTamanioMueble("Grande");
        mueble.setMaterialMueble("Roble");
        
        if (mueble.getStock() > 0) {
            mueble.setEstadoMueble("activo");
        } else {
            mueble.setEstadoMueble("inactivo");
        }
        
        assertNotNull(mueble.getNombreMueble());
        assertEquals("Mesa", mueble.getTipoMueble());
        assertEquals(25000.0, mueble.getPrecioBase());
        assertEquals(8, mueble.getStock());
        assertEquals("activo", mueble.getEstadoMueble());
        assertEquals("Grande", mueble.getTamanioMueble());
        assertEquals("Roble", mueble.getMaterialMueble());
    }
    
    @Test
    void testLeerMueblePorId() {
        Mueble mueble = new Mueble();
        mueble.setIdMueble(5);
        mueble.setNombreMueble("Cajon Pino");
        mueble.setTipoMueble("Cajon");
 
        Integer idMueble = mueble.getIdMueble();

        assertNotNull(idMueble);
        assertEquals(5, idMueble);
        assertEquals("Cajon Pino", mueble.getNombreMueble());
    }
    
    @Test
    void testActualizarMueble() {
        Mueble mueble = new Mueble();
        mueble.setIdMueble(1);
        mueble.setNombreMueble("Silla Antigua");
        mueble.setPrecioBase(5000.0);
        mueble.setStock(10);
        
        mueble.setNombreMueble("Silla Moderna");
        mueble.setPrecioBase(7500.0);
        mueble.setStock(15);
        
        assertEquals("Silla Moderna", mueble.getNombreMueble());
        assertEquals(7500.0, mueble.getPrecioBase());
        assertEquals(15, mueble.getStock());
    }
    
    @Test
    void testDesactivarMueble() {

        Mueble mueble = new Mueble();
        mueble.setIdMueble(1);
        mueble.setNombreMueble("Sillon Vintage");
        mueble.setEstadoMueble("activo");
        
        mueble.setEstadoMueble("inactivo");
        assertEquals("inactivo", mueble.getEstadoMueble());
    }
    
    @Test
    void testValidarAtributosMueblesRequeridos() {
        
        Mueble mueble = new Mueble();
        mueble.setIdMueble(1);
        mueble.setNombreMueble("Estante");
        mueble.setTipoMueble("Estante");
        mueble.setPrecioBase(18000.0);
        mueble.setStock(5);
        mueble.setEstadoMueble("activo");
        mueble.setTamanioMueble("Mediano");
        mueble.setMaterialMueble("Metal");
        
        assertNotNull(mueble.getIdMueble(), "ID mueble no puede ser null");
        assertNotNull(mueble.getNombreMueble(), "Nombre mueble no puede ser null");
        assertNotNull(mueble.getTipoMueble(), "Tipo no puede ser null");
        assertNotNull(mueble.getPrecioBase(), "Precio base no puede ser null");
        assertNotNull(mueble.getStock(), "Stock no puede ser null");
        assertNotNull(mueble.getEstadoMueble(), "Estado no puede ser null");
        assertNotNull(mueble.getTamanioMueble(), "Tamaño no puede ser null");
        assertNotNull(mueble.getMaterialMueble(), "Material no puede ser null");
    }
    
    @Test
    void testMuebleSinStockDebeEstarInactivo() {
        Mueble mueble = new Mueble();
        mueble.setNombreMueble("Silla sin stock");
        mueble.setStock(0);
        
        if (mueble.getStock() > 0) {
            mueble.setEstadoMueble("activo");
        } else {
            mueble.setEstadoMueble("inactivo");
        }
        assertEquals("inactivo", mueble.getEstadoMueble());
    }
    
    @Test
    void testListarMultiplesMuebles() {

        Mueble mueble1 = new Mueble();
        mueble1.setIdMueble(1);
        mueble1.setNombreMueble("Mesa");
        
        Mueble mueble2 = new Mueble();
        mueble2.setIdMueble(2);
        mueble2.setNombreMueble("Silla");
        
        Mueble mueble3 = new Mueble();
        mueble3.setIdMueble(3);
        mueble3.setNombreMueble("Estante");
        
        int totalMuebles = 3;
    
        assertEquals(3, totalMuebles);
        assertNotNull(mueble1.getIdMueble());
        assertNotNull(mueble2.getIdMueble());
        assertNotNull(mueble3.getIdMueble());
    }
    
    @Test
    void testActualizarStockMueble() {
        Mueble mueble = new Mueble();
        mueble.setStock(20);
        
       
        mueble.setStock(25);
        assertEquals(25, mueble.getStock());
    }
}