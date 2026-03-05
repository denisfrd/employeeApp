package com.employeeapp.employeeapp.repositories;

import com.employeeapp.employeeapp.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByEmployeeId(Long employeeId);
}