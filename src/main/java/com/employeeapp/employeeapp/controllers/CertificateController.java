package com.employeeapp.employeeapp.controllers;

import com.employeeapp.employeeapp.dtos.CertificateDTO;
import com.employeeapp.employeeapp.entities.Certificate;
import com.employeeapp.employeeapp.entities.Employee;
import com.employeeapp.employeeapp.repositories.CertificateRepository;
import com.employeeapp.employeeapp.repositories.EmployeeRepository;
import com.employeeapp.employeeapp.services.CertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateRepository certificateRepo;
    private final EmployeeRepository employeeRepo;
    private final CertificateService certificateService;

    public CertificateController(CertificateRepository certificateRepo,
                                 EmployeeRepository employeeRepo,
                                 CertificateService certificateService) {
        this.certificateRepo = certificateRepo;
        this.employeeRepo = employeeRepo;
        this.certificateService = certificateService;
    }

    // Receive certificates from GitHub Actions
    @PostMapping("/upload")
    public ResponseEntity<Certificate> uploadCertificate(@RequestBody CertificateDTO certificateDTO) {
        try {
            Employee employee = employeeRepo.findById(certificateDTO.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            Certificate certificate = new Certificate();
            certificate.setEmployee(employee);
            certificate.setCertificateData(certificateDTO.getCertificate());
            certificate.setPrivateKey(certificateDTO.getPrivateKey());
            certificate.setCsr(certificateDTO.getCsr());
            certificate.setGeneratedAt(LocalDateTime.now());
            certificate.setExpiresAt(LocalDateTime.now().plusDays(365));
            certificate.setCertificateType(certificateDTO.getCertificateType());
            certificate.setIsActive(true);

            Certificate saved = certificateRepo.save(certificate);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get certificate by employee ID
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<CertificateDTO> getCertificateByEmployee(@PathVariable Long employeeId) {
        return certificateRepo.findByEmployeeId(employeeId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Download certificate
    @GetMapping("/{id}/download")
    public ResponseEntity<String> downloadCertificate(@PathVariable Long id) {
        return certificateRepo.findById(id)
                .map(cert -> ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=certificate.crt")
                        .body(cert.getCertificateData()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Download private key
    @GetMapping("/{id}/download-key")
    public ResponseEntity<String> downloadPrivateKey(@PathVariable Long id) {
        return certificateRepo.findById(id)
                .map(cert -> ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=private.key")
                        .body(cert.getPrivateKey()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all active certificates
    @GetMapping("/active")
    public List<CertificateDTO> getAllActiveCertificates() {
        return certificateRepo.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get expired certificates
    @GetMapping("/expired")
    public List<CertificateDTO> getExpiredCertificates() {
        return certificateRepo.findExpiredCertificates().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Revoke certificate
    @PostMapping("/{id}/revoke")
    public ResponseEntity<Void> revokeCertificate(@PathVariable Long id) {
        return certificateRepo.findById(id)
                .map(certificate -> {
                    certificate.setIsActive(false);
                    certificateRepo.save(certificate);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Generate new certificate for employee
    @PostMapping("/generate/{employeeId}")
    public ResponseEntity<CertificateDTO> generateCertificate(@PathVariable Long employeeId) {
        try {
            Employee employee = employeeRepo.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            Certificate certificate = certificateService.generateCertificate(employee);
            return ResponseEntity.ok(convertToDTO(certificate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Upload certificate file
    @PostMapping("/upload-file")
    public ResponseEntity<String> uploadCertificateFile(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("certificate") MultipartFile certificateFile,
            @RequestParam(value = "privateKey", required = false) MultipartFile privateKeyFile) {
        try {
            certificateService.processUploadedFiles(employeeId, certificateFile, privateKeyFile);
            return ResponseEntity.ok("Certificate uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    private CertificateDTO convertToDTO(Certificate certificate) {
        CertificateDTO dto = new CertificateDTO();
        dto.setEmployeeId(certificate.getEmployee().getEmployeeId());
        dto.setCertificate(certificate.getCertificateData());
        dto.setPrivateKey(certificate.getPrivateKey());
        dto.setCsr(certificate.getCsr());
        dto.setGeneratedAt(certificate.getGeneratedAt());
        dto.setExpiresAt(certificate.getExpiresAt());
        dto.setCertificateType(certificate.getCertificateType());
        return dto;
    }
}