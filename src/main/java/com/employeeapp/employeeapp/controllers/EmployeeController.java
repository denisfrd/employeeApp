package com.employeeapp.employeeapp.controllers;

import com.employeeapp.employeeapp.entities.Employee;
import com.employeeapp.employeeapp.repositories.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {



    private final EmployeeRepository employeeRepo;

    public EmployeeController(EmployeeRepository employeeRepo) {
        this.employeeRepo = employeeRepo;
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

        Employee savedEmployee = employeeRepo.save(employee);
        return ResponseEntity.ok(employeeRepo.save(employee));
    }

    //Fetches all employees
    @GetMapping("/fetch/all")
    public List<Employee> getAllEmployees(){
        return employeeRepo.findAll();
    }

    //Fetches employee by ID
    @GetMapping("/fetch")
    public ResponseEntity<Employee> getEmployeeById(@RequestHeader Long employeeId){
        Optional<Employee> employee = employeeRepo.findById(employeeId);
        if(employee.isPresent()){
            return ResponseEntity.ok(employee.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    //Deletes an employee by ID
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteEmployee(@RequestHeader Long employeeId){
        if(!employeeRepo.existsById(employeeId)){
            return ResponseEntity.notFound().build();
        }
        employeeRepo.deleteById(employeeId);
        return ResponseEntity.noContent().build();
    }

    //Deletes all employees
    @DeleteMapping("/delete/all")
    public ResponseEntity<Void> deleteAllEmployees(){
        employeeRepo.deleteAll();
        return ResponseEntity.noContent().build();
    }

   

}
