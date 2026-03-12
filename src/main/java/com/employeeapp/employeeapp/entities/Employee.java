package com.employeeapp.employeeapp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;

@Entity
public class Employee {

    @Id
    private Long employeeId;
    private String employeeName;
    private String department;

    @Lob
    private String certificate;

    @Lob
    private String privateKey;

    @Lob
    private String csr;

    private LocalDateTime certificateGeneratedAt;
    private LocalDateTime certificateExpiresAt;
    private Boolean certificateEnabled = true;

    public Employee() {}

    public Employee(Long employeeId, String employeeName, String department){
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
    }

    //Getters/setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCertificate() { return certificate; }
    public void setCertificate(String certificate) { this.certificate = certificate; }

    public String getPrivateKey() { return privateKey; }
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }

    public String getCsr() { return csr; }
    public void setCsr(String csr) { this.csr = csr; }

    public LocalDateTime getCertificateGeneratedAt() { return certificateGeneratedAt; }
    public void setCertificateGeneratedAt(LocalDateTime certificateGeneratedAt) {
        this.certificateGeneratedAt = certificateGeneratedAt;
    }

    public LocalDateTime getCertificateExpiresAt() { return certificateExpiresAt; }
    public void setCertificateExpiresAt(LocalDateTime certificateExpiresAt) {
        this.certificateExpiresAt = certificateExpiresAt;
    }

    public Boolean getCertificateEnabled() { return certificateEnabled; }
    public void setCertificateEnabled(Boolean certificateEnabled) {
        this.certificateEnabled = certificateEnabled;
    }
}