package com.example.muebles.Repository;
import com.example.muebles.Entity.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MuebleRepository extends JpaRepository <Mueble, Integer>{
    
}
