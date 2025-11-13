package com.example.muebles.Services.Strategy;
import com.example.muebles.Entity.*;

public interface PrecioStrategy {
    double calcularPrecio(Mueble mueble, Variante variante);
}
