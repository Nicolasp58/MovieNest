package com.example.demo.repositories;

import com.example.demo.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;  // <--- Asegúrate de que esta línea está presente

@Repository  // <--- Añade esta anotación
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
