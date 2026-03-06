package com.employeeapp.employeeapp.dtos;

import java.time.LocalDateTime;

public class CertificateDTO {
    private Long employeeId;
    private String certificate;
    private String privateKey;
    private String csr;
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
    private String certificateType;

    // Constructors
    public CertificateDTO() {}

    public CertificateDTO(Long employeeId, String certificate, String privateKey,
                          String csr, LocalDateTime generatedAt,
                          LocalDateTime expiresAt, String certificateType) {
        this.employeeId = employeeId;
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.csr = csr;
        this.generatedAt = generatedAt;
        this.expiresAt = expiresAt;
        this.certificateType = certificateType;
    }

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getCertificate() { return certificate; }
    public void setCertificate(String certificate) { this.certificate = certificate; }

    public String getPrivateKey() { return privateKey; }
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }

    public String getCsr() { return csr; }
    public void setCsr(String csr) { this.csr = csr; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getCertificateType() { return certificateType; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }
}