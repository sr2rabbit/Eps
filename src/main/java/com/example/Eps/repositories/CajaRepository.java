package com.example.Eps.repositories;

import com.example.Eps.entities.Caja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Long> {
    
    // Método para buscar una caja por su número (ej. Caja 1, Caja 2)
    Optional<Caja> findByNumeroCaja(Integer numeroCaja);
}
