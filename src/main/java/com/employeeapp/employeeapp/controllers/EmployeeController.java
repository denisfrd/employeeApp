package com.employeeapp.employeeapp.controllers;

import com.employeeapp.employeeapp.entities.Employee;
import com.employeeapp.employeeapp.repositories.EmployeeRepository;
import com.employeeapp.employeeapp.repositories.CertificateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepo;
    private final CertificateRepository certificateRepo;

    public EmployeeController(EmployeeRepository employeeRepo, CertificateRepository certificateRepo) {
        this.employeeRepo = employeeRepo;
        this.certificateRepo = certificateRepo;
    }

    @PostMapping("/createEmployee")
    public ResponseEntity<Employee> createEmployee(@RequestHeader("employeeId") Long employeeId,
                                                   @RequestHeader("employeeName") String employeeName,
                                                   @RequestHeader("department") String department) {
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setEmployeeName(employeeName);
        employee.setDepartment(department);

        Employee savedEmployee = employeeRepo.save(employee);
        return ResponseEntity.ok(savedEmployee);
    }

    @GetMapping("/fetch/all")
    public List<Employee> getAllEmployees(){
        return employeeRepo.findAll();
    }

    @GetMapping("/fetch")
    public ResponseEntity<Employee> getEmployeeById(@RequestHeader Long employeeId){
        Optional<Employee> employee = employeeRepo.findById(employeeId);
        return employee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteEmployee(@RequestHeader Long employeeId){
        if(!employeeRepo.existsById(employeeId)){
            return ResponseEntity.notFound().build();
        }

        //Deletes associated certificates
        certificateRepo.findByEmployeeId(employeeId).ifPresent(certificateRepo::delete);

        employeeRepo.deleteById(employeeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<Void> deleteAllEmployees(){
        //Deletes all certificates
        certificateRepo.deleteAll();
        employeeRepo.deleteAll();
        return ResponseEntity.noContent().build();
    }

    //Fetches employee with certificate
    @GetMapping("/{id}/with-certificate")
    public ResponseEntity<Employee> getEmployeeWithCertificate(@PathVariable Long id){
        Optional<Employee> employee = employeeRepo.findById(id);
        if (employee.isPresent()) {
            certificateRepo.findByEmployeeId(id).ifPresent(cert -> {
                // You might want to add certificate info to response
                employee.get().setCertificate(cert.getCertificateData());
            });
            return ResponseEntity.ok(employee.get());
        }
        return ResponseEntity.notFound().build();
    }
}