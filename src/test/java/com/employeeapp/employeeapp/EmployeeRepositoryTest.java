package com.employeeapp.employeeapp;

import com.employeeapp.employeeapp.entities.Employee;
import com.employeeapp.employeeapp.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;

    @Test
    void testSaveAndFind() {
        Employee emp = new Employee(1L,"John", "IT");
        repository.save(emp);

        Optional<Employee> found = repository.findById(1L);
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getEmployeeName());
    }

    @Test
    void testDeleteNonExisting() {
        repository.deleteById(999L); // Won't throw an exception
        assertTrue(repository.findById(999L).isEmpty());
    }
}