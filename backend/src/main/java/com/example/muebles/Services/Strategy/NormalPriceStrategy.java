package com.example.muebles.Services.Strategy;
import com.example.muebles.Entity.*;

public class NormalPriceStrategy implements PrecioStrategy {
    @Override
    public double calcularPrecio(Mueble mueble, Variante variante) {
        // Retorna solo el precio base del mueble, sin modificaciones
        return mueble.getPrecioBase();
    }
}
