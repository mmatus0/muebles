package com.example.muebles.Services;
import com.example.muebles.Entity.*;
import com.example.muebles.Services.Strategy.*;
import org.springframework.stereotype.Service;

@Service
public class PrecioService {
    public double calcularPrecioUnitario(Mueble mueble, Variante variante) {
        PrecioStrategy newEstrategia;
        
        // Seleccionar estrategia según si tiene variante o no
        if (variante == null || variante.getNombreVariante().equalsIgnoreCase("Normal")) {
            newEstrategia = new NormalPriceStrategy();
        } else {
            newEstrategia = new PriceConVarianteStrategy();
        }
        
        return newEstrategia.calcularPrecio(mueble, variante);
    }

    public double calcularSubtotal(Mueble mueble, Variante variante, int cantidad) {
        double precioUnitario = calcularPrecioUnitario(mueble, variante);
        return precioUnitario * cantidad;
    }

    public double obtenerPrecioAgregado(Variante variante) {
        if (variante == null || variante.getNombreVariante().equalsIgnoreCase("Normal")) {
            return 0.0;
        }
        return variante.getPrecioAgregado();
    }

    public double calcularPorcentajeIncremento(Mueble mueble, Variante variante) {
        if (mueble.getPrecioBase() == 0) {
            return 0.0;
        }
        
        double precioAgregado = obtenerPrecioAgregado(variante);
        return (precioAgregado / mueble.getPrecioBase()) * 100;
    }
}
