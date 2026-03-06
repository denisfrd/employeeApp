package com.employeeapp.employeeapp.repositories;

import com.employeeapp.employeeapp.entities.Certificate;
import com.employeeapp.employeeapp.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByEmployee(Employee employee);

    List<Certificate> findByIsActiveTrue();

    @Query("SELECT c FROM Certificate c WHERE c.expiresAt < CURRENT_DATE")
    List<Certificate> findExpiredCertificates();

    @Query("SELECT c FROM Certificate c WHERE c.employee.employeeId = :employeeId")
    Optional<Certificate> findByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT c FROM Certificate c WHERE c.certificateType = :type")
    List<Certificate> findByCertificateType(@Param("type") String type);
}