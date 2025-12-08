package com.example.muebles.Services.Strategy;
import com.example.muebles.Entity.*;

public class PriceConVarianteStrategy implements PrecioStrategy {
    @Override
    public double calcularPrecio(Mueble mueble, Variante variante) {
        if (variante == null) {
            // Si no hay variante, retorna solo precio base
            return mueble.getPrecioBase();
        }
        
        // Precio base + precio agregado de la variante
        return mueble.getPrecioBase() + variante.getPrecioAgregado();
    }
}
