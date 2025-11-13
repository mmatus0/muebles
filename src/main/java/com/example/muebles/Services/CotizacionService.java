package com.example.muebles.Services;

import com.example.muebles.Entity.*;
import com.example.muebles.Repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CotizacionService {
    @Autowired
    private CotizacionRepository cotizacionRepository;
    
    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;
    
    @Autowired
    private MuebleService muebleService;
    
    @Autowired
    private VarianteService varianteService;
    
    @Autowired
    private PrecioService precioService;

    public CotizacionService(CotizacionRepository cotizacionRepository, 
    DetalleCotizacionRepository detalleCotizacionRepository, MuebleService muebleService, 
    VarianteService varianteService, PrecioService precioService){

        this.cotizacionRepository = cotizacionRepository;
        this.detalleCotizacionRepository = detalleCotizacionRepository;
        this.muebleService = muebleService;
        this.precioService = precioService;
        this.varianteService = varianteService;
    }

    //USO DE PATRÓN BUILDER
    @Transactional
    public Cotizacion crearCotizacion() {
        Cotizacion cotizacion = Cotizacion.builder()
            .setFechaCotizacion(LocalDate.now())
            .setEstadoCotizacion("PENDIENTE")
            .setPrecioFinal(0.0)
            .build();
        
        return cotizacionRepository.save(cotizacion);
    }

    public DetalleCotizacion agregarDetalle(Integer idCotizacion, Integer idMueble, Integer idVariante, int cantidad) throws Exception {
        if (cantidad <= 0) {
            throw new Exception("La cantidad debe ser mayor que 0");
        }
        
        Cotizacion cotizacion = listarCotizacion(idCotizacion);
        Mueble mueble = muebleService.listarMueble(idMueble);
        Variante variante = null;
        
        if (idVariante != null) {
            variante = varianteService.listarVariante(idVariante);
        }
        
        if (!cotizacion.getEstadoCotizacion().equals("PENDIENTE")) {
            throw new Exception("No se puede modificar");
        }
        
        if (!muebleService.verificarStock(idMueble, cantidad)) {
            throw new Exception("Stock insuficiente de mueble: " + mueble.getNombreMueble());
        }
        
        //USO DE precioService
        double precioUnitario = precioService.calcularPrecioUnitario(mueble, variante);
        double subtotal = precioService.calcularSubtotal(mueble, variante, cantidad);
        
        DetalleCotizacion detalle = new DetalleCotizacion();
        detalle.setCotizacion(cotizacion);
        detalle.setMueble(mueble);
        detalle.setVariante(variante);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setSubtotal(subtotal);
        
        detalle = detalleCotizacionRepository.save(detalle);
        actualizarPrecioFinal(idCotizacion);
        
        return detalle;
    }

    @Transactional
    public void actualizarPrecioFinal(Integer idCotizacion) throws Exception {
        Cotizacion cotizacion = listarCotizacion(idCotizacion);
        List<DetalleCotizacion> detalles = detalleCotizacionRepository.findByCotizacionIdCotizacion(idCotizacion);
        
        double precioTotal = detalles.stream().mapToDouble(DetalleCotizacion::getSubtotal).sum();
        cotizacion.setPrecioFinal(precioTotal);
        cotizacionRepository.save(cotizacion);
    }

    //OPERACIONES DEL TIPO CRUD

    public Cotizacion listarCotizacion(Integer id) throws Exception {
        return cotizacionRepository.findById(id).orElseThrow(() -> new Exception("Cotización no encontrada"));
    }

    public List<Cotizacion> listarTodasCotizaciones() {
        return cotizacionRepository.findAll();
    }    
}