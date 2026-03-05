package com.employeeapp.employeeapp.repositories;

import com.employeeapp.employeeapp.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {


    @Query("SELECT c FROM Certificate c WHERE c.employee.employeeId = :empId")
    List<Certificate> findByEmployeeId(@Param("empId") Long employeeId);


    List<Certificate> findByEmployee_EmployeeId(Long employeeId);
}