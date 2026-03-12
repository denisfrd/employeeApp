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
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    //Receives certificates from GitHub Actions
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

    //Fetches certificate by employee ID
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<CertificateDTO> getCertificateByEmployee(@PathVariable Long employeeId) {
        return certificateRepo.findByEmployeeId(employeeId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Downloads certificate
    @GetMapping("/{id}/download")
    public ResponseEntity<String> downloadCertificate(@PathVariable Long id) {
        return certificateRepo.findById(id)
                .map(cert -> ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=certificate.crt")
                        .body(cert.getCertificateData()))
                .orElse(ResponseEntity.notFound().build());
    }

    //Downloads private key
    @GetMapping("/{id}/download-key")
    public ResponseEntity<String> downloadPrivateKey(@PathVariable Long id) {
        return certificateRepo.findById(id)
                .map(cert -> ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=private.key")
                        .body(cert.getPrivateKey()))
                .orElse(ResponseEntity.notFound().build());
    }

    //Fetches all active certificates
    @GetMapping("/active")
    public List<CertificateDTO> getAllActiveCertificates() {
        return certificateRepo.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //Fetches expired certificates
    @GetMapping("/expired")
    public List<CertificateDTO> getExpiredCertificates() {
        return certificateRepo.findExpiredCertificates().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //Revokes certificate
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

    //Generates new certificate for employee
    @PostMapping("/generate/{employeeId}")
    public ResponseEntity<CertificateDTO> generateCertificate(@PathVariable Long employeeId) {
        try {
            Employee employee = employeeRepo.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));

            Certificate certificate = certificateService.generateCertificate(employee);
            return ResponseEntity.ok(convertToDTO(certificate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Uploads certificate
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

    //Converts to DTO
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

    //Checks if certificate exists for an employee
    @GetMapping("/check/{employeeId}")
    public ResponseEntity<Map<String, Object>> checkCertificateExists(@PathVariable Long employeeId) {
        Optional<Certificate> certificate = certificateRepo.findByEmployeeId(employeeId);

        //Response object
        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeId);
        response.put("certificateExists", certificate.isPresent());

        if (certificate.isPresent()) {
            Certificate cert = certificate.get();
            response.put("certificateId", cert.getId());
            response.put("generatedAt", cert.getGeneratedAt());
            response.put("expiresAt", cert.getExpiresAt());
            response.put("isActive", cert.getIsActive());
            response.put("certificateType", cert.getCertificateType());
            response.put("certificatePreview",
                    cert.getCertificateData() != null ?
                            cert.getCertificateData().substring(0, Math.min(100, cert.getCertificateData().length())) + "..." :
                            null);
        }

        return ResponseEntity.ok(response);
    }

    //Fetches all certificates in database
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllCertificates() {
        List<Certificate> certificates = certificateRepo.findAll();

        List<Map<String, Object>> response = certificates.stream().map(cert -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cert.getId());
            map.put("employeeId", cert.getEmployee().getEmployeeId());
            map.put("employeeName", cert.getEmployee().getEmployeeName());
            map.put("generatedAt", cert.getGeneratedAt());
            map.put("expiresAt", cert.getExpiresAt());
            map.put("isActive", cert.getIsActive());
            map.put("certificateType", cert.getCertificateType());
            map.put("hasCertificate", cert.getCertificateData() != null);
            map.put("hasPrivateKey", cert.getPrivateKey() != null);
            map.put("hasCsr", cert.getCsr() != null);
            map.put("certificateSize",
                    cert.getCertificateData() != null ? cert.getCertificateData().length() : 0);
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    //Verifies certificate details
    @GetMapping("/verify/{certificateId}")
    public ResponseEntity<Map<String, Object>> verifyCertificate(@PathVariable Long certificateId) {
        return certificateRepo.findById(certificateId)
                .map(cert -> {
                    Map<String, Object> verification = new HashMap<>();
                    verification.put("id", cert.getId());
                    verification.put("employeeId", cert.getEmployee().getEmployeeId());
                    verification.put("isValid", cert.getIsActive() &&
                            cert.getExpiresAt().isAfter(LocalDateTime.now()));
                    verification.put("expiresIn",
                            ChronoUnit.DAYS.between(LocalDateTime.now(), cert.getExpiresAt()) + " days");
                    verification.put("certificateFormat", detectCertificateFormat(cert.getCertificateData()));
                    verification.put("privateKeyFormat", detectKeyFormat(cert.getPrivateKey()));

                    //Parses certificate to get details
                    try {
                        if (cert.getCertificateData() != null) {
                            String certData = cert.getCertificateData();
                            //Looks for common certificate fields
                            if (certData.contains("BEGIN CERTIFICATE")) {
                                verification.put("type", "X.509");
                                //Extracts subject if possible
                                Pattern subjectPattern = Pattern.compile("Subject: (.*?)(?=\\n|$)");
                                Matcher matcher = subjectPattern.matcher(certData);
                                if (matcher.find()) {
                                    verification.put("subject", matcher.group(1));
                                }
                            }
                        }
                    } catch (Exception e) {
                        verification.put("parseError", e.getMessage());
                    }

                    return ResponseEntity.ok(verification);
                }).orElse(ResponseEntity.notFound().build());
    }

    //Counts certificates by status
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCertificateStats() {
        List<Certificate> all = certificateRepo.findAll();
        long active = certificateRepo.findByIsActiveTrue().size();
        long expired = certificateRepo.findExpiredCertificates().size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCertificates", all.size());
        stats.put("activeCertificates", active);
        stats.put("expiredCertificates", expired);
        stats.put("certificatesByEmployee", all.stream()
                .collect(Collectors.groupingBy(
                        cert -> cert.getEmployee().getEmployeeId(),
                        Collectors.counting()
                )));

        return ResponseEntity.ok(stats);
    }

    private String detectCertificateFormat(String certData) {
        if (certData == null) return "NONE";
        if (certData.contains("BEGIN CERTIFICATE")) return "PEM";
        if (certData.startsWith("MII") || certData.matches("^[A-Za-z0-9+/=]+$")) return "BASE64";
        return "UNKNOWN";
    }

    private String detectKeyFormat(String keyData) {
        if (keyData == null) return "NONE";
        if (keyData.contains("BEGIN PRIVATE KEY")) return "PEM PKCS#8";
        if (keyData.contains("BEGIN RSA PRIVATE KEY")) return "PEM RSA";
        return "UNKNOWN";
    }
}
