package com.example.muebles.Services;

import com.example.muebles.Entity.Cotizacion;
import com.example.muebles.Entity.DetalleCotizacion;
import com.example.muebles.Repository.CotizacionRepository;
import com.example.muebles.Repository.DetalleCotizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VentaService {
    
    @Autowired
    private CotizacionRepository cotizacionRepository;
    
    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;
    
    @Autowired
    private MuebleService muebleService;

    public VentaService(CotizacionRepository cotizacionRepository,
                       DetalleCotizacionRepository detalleCotizacionRepository,
                       MuebleService muebleService) {
        this.cotizacionRepository = cotizacionRepository;
        this.detalleCotizacionRepository = detalleCotizacionRepository;
        this.muebleService = muebleService;
    }
    
    @Transactional
    public Cotizacion confirmarVenta(Integer idCotizacion) throws Exception {
        
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
            .orElseThrow(() -> new Exception("Cotización no encontrada con ID: " + idCotizacion));
        
        if (!cotizacion.getEstadoCotizacion().equals("PENDIENTE")) {
            throw new Exception("La cotización ya fue procesada. Estado actual: " + cotizacion.getEstadoCotizacion());
        }
        
        List<DetalleCotizacion> detalles = detalleCotizacionRepository.findByCotizacionIdCotizacion(idCotizacion);
        
        if (detalles.isEmpty()) {
            throw new Exception("La cotización no tiene items. No se puede confirmar venta.");
        }
        
        // 4. VALIDAR STOCK ANTES DE PROCESAR (muy importante)
        for (DetalleCotizacion detalle : detalles) {
            boolean hayStock = muebleService.verificarStock(
                detalle.getMueble().getIdMueble(), 
                detalle.getCantidad()
            );
            
            if (!hayStock) {
                throw new Exception("Stock insuficiente para: " + detalle.getMueble().getNombreMueble() 
                    + ". Stock disponible: " + detalle.getMueble().getStock() 
                    + ", cantidad solicitada: " + detalle.getCantidad());
            }
        }
        
        // DECREMENTAR STOCK
        for (DetalleCotizacion detalle : detalles) {
            muebleService.disminuirStock(detalle.getMueble().getIdMueble(), detalle.getCantidad());
        }
        
        // CONFIRMAR COTIZACIÓN COMO VENTA
        cotizacion.setEstadoCotizacion("CONFIRMADA");
        return cotizacionRepository.save(cotizacion);
    }

    // LISTAR TODAS LAS VENTAS CONFIRMADAS
    public List<Cotizacion> listarVentas() {
        return cotizacionRepository.findByEstadoCotizacion("CONFIRMADA");
    }
    
    
    public Cotizacion obtenerVentaPorId(Integer idCotizacion) throws Exception {
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
            .orElseThrow(() -> new Exception("Venta no encontrada"));
        
        if (!cotizacion.getEstadoCotizacion().equals("CONFIRMADA")) {
            throw new Exception("La cotización con ID " + idCotizacion + " no es una venta confirmada");
        }
        
        return cotizacion;
    }
    
    public double calcularTotalVentas() {
        List<Cotizacion> ventas = listarVentas();
        return ventas.stream().mapToDouble(Cotizacion::getPrecioFinal).sum();
    }
    
    public Integer contarVentas() {
        return cotizacionRepository.countByEstadoCotizacion("CONFIRMADA");
    }
    
    public boolean validarStockParaVenta(Integer idCotizacion) throws Exception {
        List<DetalleCotizacion> detalles = detalleCotizacionRepository.findByCotizacionIdCotizacion(idCotizacion);
        
        for (DetalleCotizacion detalle : detalles) {
            boolean hayStock = muebleService.verificarStock(
                detalle.getMueble().getIdMueble(), 
                detalle.getCantidad()
            );
            
            if (!hayStock) {
                return false;
            }
        }
        
        return true;
    }
}