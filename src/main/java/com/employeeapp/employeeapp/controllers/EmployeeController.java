package com.employeeapp.employeeapp.controllers;

import com.employeeapp.employeeapp.entities.Employee;
import com.employeeapp.employeeapp.repositories.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository){
        this.repository = repository;
    }

    //Creates one or multiple employees
    @PostMapping("/createEmployee")
    public ResponseEntity<Employee> createEmployee(@RequestHeader("employeeId") Long employeeId,
                                                   @RequestHeader("employeeName") String employeeName,
                                                   @RequestHeader("department") String department) {

        Employee employee = new Employee();

        employee.setEmployeeId(employeeId);
        employee.setEmployeeName(employeeName);
        employee.setDepartment(department);

        Employee savedEmployee = repository.save(employee);
        return ResponseEntity.ok(savedEmployee);
    }

    //Uploads certificate
    @PostMapping("/upload-certificate")
    public ResponseEntity<Employee> uploadCertificate(
            @RequestHeader("employeeId") Long employeeId,
            @RequestHeader("employeeName") String employeeName,
            @RequestBody Map<String, Object> certificateData){

        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setEmployeeName(employeeName);
        employee.setDepartment("IT-Security");

        Employee savedEmployee = repository.save(employee);

        return ResponseEntity.ok(savedEmployee);

    }

    //Fetches all employees
    @GetMapping("/fetch/all")
    public List<Employee> getAllEmployees(){
        return repository.findAll();
    }

    //Fetches employee by ID
    @GetMapping("/fetch")
    public ResponseEntity<Employee> getEmployeeById(@RequestHeader Long employeeId){
        Optional<Employee> employee = repository.findById(employeeId);
        if(employee.isPresent()){
            return ResponseEntity.ok(employee.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    //Deletes an employee by ID
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteEmployee(@RequestHeader Long employeeId){
        if(!repository.existsById(employeeId)){
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(employeeId);
        return ResponseEntity.noContent().build();
    }

    //Deletes all employees
    @DeleteMapping("/delete/all")
    public ResponseEntity<Void> deleteAllEmployees(){
        repository.deleteAll();
        return ResponseEntity.noContent().build();
    }

}
