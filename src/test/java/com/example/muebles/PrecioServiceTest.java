package com.example.muebles;

import com.example.muebles.Entity.*;
import com.example.muebles.Services.PrecioService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el servicio de precios (variantes)
 */
class PrecioServiceTest {
    
    @Test
    void testPrecioSinVariante() {
        PrecioService precioService = new PrecioService();
        Mueble mueble = new Mueble();
        mueble.setPrecioBase(10000.0);
        double precio = precioService.calcularPrecioUnitario(mueble, null);
        assertEquals(10000.0, precio);
    }
    
    @Test
    void testPrecioConVariante() {
        PrecioService precioService = new PrecioService();
        
        Mueble mueble = new Mueble();
        mueble.setPrecioBase(10000.0);
        
        Variante variante = new Variante();
        variante.setNombreVariante("Barniz Premium");
        variante.setPrecioAgregado(2000.0);
        double precio = precioService.calcularPrecioUnitario(mueble, variante);
        assertEquals(12000.0, precio);
    }
    
    @Test
    void testCalcularSubtotalConCantidad() {
        PrecioService precioService = new PrecioService();
        Mueble mueble = new Mueble();
        mueble.setPrecioBase(5000.0);
    
        double subtotal = precioService.calcularSubtotal(mueble, null, 3);
        assertEquals(15000.0, subtotal);
    }
    
    @Test
    void testSubtotalConVarianteYCantidad() {
        PrecioService precioService = new PrecioService();
        
        Mueble mueble = new Mueble();
        mueble.setPrecioBase(8000.0);
        
        Variante variante = new Variante();
        variante.setNombreVariante("Cojines de Seda");
        variante.setPrecioAgregado(1500.0);
        double subtotal = precioService.calcularSubtotal(mueble, variante, 2);

        assertEquals(19000.0, subtotal);
    }
    
    @Test
    void testVarianteNormalNoAgregaPrecio() {
        PrecioService precioService = new PrecioService();
        
        Mueble mueble = new Mueble();
        mueble.setPrecioBase(12000.0);
        
        Variante varianteNormal = new Variante();
        varianteNormal.setNombreVariante("Normal");
        varianteNormal.setPrecioAgregado(0.0);
        double precio = precioService.calcularPrecioUnitario(mueble, varianteNormal);

        assertEquals(12000.0, precio);
    }
    
    @Test
    void testObtenerPrecioAgregadoVariante() {
        PrecioService precioService = new PrecioService();
        
        Variante variante = new Variante();
        variante.setNombreVariante("Ruedas");
        variante.setPrecioAgregado(3000.0);
        double precioAgregado = precioService.obtenerPrecioAgregado(variante);

        assertEquals(3000.0, precioAgregado);
    }
    
    @Test
    void testCalcularPorcentajeIncremento() {
        PrecioService precioService = new PrecioService();
        
        Mueble mueble = new Mueble();
        mueble.setPrecioBase(10000.0);
        
        Variante variante = new Variante();
        variante.setNombreVariante("Acabado Premium");
        variante.setPrecioAgregado(2000.0);
        
        double porcentaje = precioService.calcularPorcentajeIncremento(mueble, variante);
        assertEquals(20.0, porcentaje);
    }
}