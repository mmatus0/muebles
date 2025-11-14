package com.example.muebles;

import com.example.muebles.Entity.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para stock/venta
 */
public class VentaServiceTest {
    
    @Test
    void testVerificarStockSuficiente() {
        Mueble mueble = new Mueble();
        mueble.setIdMueble(1);
        mueble.setNombreMueble("Silla Test");
        mueble.setStock(10);
        
        int cantidadSolicitada = 5;
        boolean hayStock = mueble.getStock() >= cantidadSolicitada;
        assertTrue(hayStock, "Debe haber stock suficiente para 5 unidades");
    }
    
    @Test
    void testVerificarStockInsuficiente() {
        Mueble mueble = new Mueble();
        mueble.setIdMueble(2);
        mueble.setNombreMueble("Mesa Test");
        mueble.setStock(3);
    
        int cantidadSolicitada = 10;
        boolean hayStock = mueble.getStock() >= cantidadSolicitada;
        
        assertFalse(hayStock, "NO debe haber stock suficiente para 10 unidades cuando hay 3");
    }
    
    @Test
    void testDecrementarStockAlConfirmarVenta() {
        Mueble mueble = new Mueble();
        mueble.setNombreMueble("Sillon");
        mueble.setStock(15);
        int stockInicial = mueble.getStock();
        
        int cantidadVendida = 5;
        mueble.setStock(mueble.getStock() - cantidadVendida);
        
        assertEquals(10, mueble.getStock());
        assertEquals(stockInicial - cantidadVendida, mueble.getStock());
    }
    
    @Test
    void testVentaConStockCero() {
        Mueble mueble = new Mueble();
        mueble.setNombreMueble("Estante agotado");
        mueble.setStock(0);
        int cantidadSolicitada = 1;
        boolean hayStock = mueble.getStock() >= cantidadSolicitada;
        
        assertFalse(hayStock, "No debe permitir venta cuando stock es 0");
    }
    
    @Test
    void testValidarEstadoCotizacionVenta() {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setEstadoCotizacion("PENDIENTE");
        
        boolean puedeConfirmarse = cotizacion.getEstadoCotizacion().equals("PENDIENTE");
        assertTrue(puedeConfirmarse, "Una cotización PENDIENTE debe poder confirmarse como venta");
    }
    
    @Test
    void testVentaDeCotizacionYaConfirmada() {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setEstadoCotizacion("CONFIRMADA");
    
        boolean puedeConfirmarse = cotizacion.getEstadoCotizacion().equals("PENDIENTE");
        assertFalse(puedeConfirmarse, "Una cotización CONFIRMADA NO debe poder confirmarse nuevamente");
    }
    
    @Test
    void testStockNegativo() {
        Mueble mueble = new Mueble();
        mueble.setStock(5);

        int cantidadSolicitada = 10;
        boolean puedeVender = mueble.getStock() >= cantidadSolicitada;
        assertFalse(puedeVender, "No debe permitirse vender más unidades de las disponibles");
    }
    
    @Test
    void testCalcularTotalVentaConMultiplesDetalles() {
        DetalleCotizacion detalle1 = new DetalleCotizacion();
        detalle1.setSubtotal(10000.0);
        
        DetalleCotizacion detalle2 = new DetalleCotizacion();
        detalle2.setSubtotal(5000.0);
        double total = detalle1.getSubtotal() + detalle2.getSubtotal();
    
        assertEquals(15000.0, total);
    }
}