package com.employeeapp.employeeapp.repositories;

import com.employeeapp.employeeapp.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
