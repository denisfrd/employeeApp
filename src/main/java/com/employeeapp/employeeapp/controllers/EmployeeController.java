package com.employeeapp.employeeapp.controllers;

import com.employeeapp.employeeapp.entities.Employee;
import com.employeeapp.employeeapp.entities.Certificate;
import com.employeeapp.employeeapp.repositories.CertificateRepository;
import com.employeeapp.employeeapp.repositories.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {



    private final EmployeeRepository employeeRepo;
    private final CertificateRepository certRepo;

    public EmployeeController(EmployeeRepository employeeRepo, CertificateRepository certRepo) {
        this.employeeRepo = employeeRepo;
        this.certRepo = certRepo;
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

    // CERTS

    @PostMapping("/{employeeId}/certs")
    public ResponseEntity<?> uploadCertificate(@PathVariable Long employeeId,
                                               @RequestBody Map<String, String> data) {
        //Creates or finds employee
        Employee emp = employeeRepo.findById(employeeId).orElseGet(() -> {
            Employee newEmp = new Employee();
            newEmp.setEmployeeId(employeeId);
            newEmp.setEmployeeName(data.getOrDefault("name", "Auto Created"));
            newEmp.setDepartment("IT");
            return employeeRepo.save(newEmp);
        });

        //Creates and save certificate
        Certificate cert = new Certificate();
        cert.setEmployee(emp);
        cert.setPrivateKey(data.get("key"));
        cert.setCertificate(data.get("cert"));
        cert.setCsr(data.get("csr"));
        cert.setCommonName(data.get("cn"));
        cert.setExpiryDate(LocalDateTime.now().plusYears(1));

        certRepo.save(cert);

        return ResponseEntity.ok(Map.of(
                "message", "Certificate uploaded successfully",
                "employeeId", employeeId,
                "certificateId", cert.getId()
        ));
    }

    //Fetch all certificates
    @GetMapping("/{employeeId}/certs")
    public ResponseEntity<List<Certificate>> getEmployeeCertificates(@PathVariable Long empId) {
        List<Certificate> certs = certRepo.findByEmployeeId(empId);
        return certs.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(certs);
    }

    //Fetch specific certificate
    @GetMapping("/certs/{certId}")
    public ResponseEntity<Certificate> getCertificate(@PathVariable Long certId) {
        return certRepo.findById(certId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Validates certificate
    @GetMapping("/certs/{certId}/validate")
    public ResponseEntity<Map<String, Object>> validateCertificate(@PathVariable Long certId) {
        Certificate cert = certRepo.findById(certId).orElse(null);
        if (cert == null) return ResponseEntity.notFound().build();

        boolean isValid = cert.getExpiryDate() != null && cert.getExpiryDate().isAfter(LocalDateTime.now());

        return ResponseEntity.ok(Map.of(
                "valid", isValid,
                "expiryDate", cert.getExpiryDate(),
                "daysLeft", isValid ? LocalDateTime.now().until(cert.getExpiryDate(), java.time.temporal.ChronoUnit.DAYS) : 0
        ));
    }

}
