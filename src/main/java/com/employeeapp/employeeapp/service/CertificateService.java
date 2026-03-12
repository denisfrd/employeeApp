package com.employeeapp.employeeapp.services;

import com.employeeapp.employeeapp.entities.Certificate;
import com.employeeapp.employeeapp.entities.Employee;
import com.employeeapp.employeeapp.repositories.CertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepo;

    public CertificateService(CertificateRepository certificateRepo) {
        this.certificateRepo = certificateRepo;
    }

    public Certificate generateCertificate(Employee employee) throws Exception {
        //Calls OpenSSL commands + creates placeholder
        Certificate certificate = new Certificate();
        certificate.setEmployee(employee);
        certificate.setGeneratedAt(LocalDateTime.now());
        certificate.setExpiresAt(LocalDateTime.now().plusDays(365));
        certificate.setCertificateType("CLIENT");
        certificate.setIsActive(true);

        return certificateRepo.save(certificate);
    }

    //Processes the uploaded files
    public void processUploadedFiles(Long employeeId, MultipartFile certFile, MultipartFile keyFile) throws Exception {
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);

        String certContent = new BufferedReader(new InputStreamReader(certFile.getInputStream()))
                .lines().collect(Collectors.joining("\n"));

        Certificate certificate = new Certificate();
        certificate.setEmployee(employee);
        certificate.setCertificateData(certContent);

        if (keyFile != null) {
            String keyContent = new BufferedReader(new InputStreamReader(keyFile.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
            certificate.setPrivateKey(keyContent);
        }

        certificate.setGeneratedAt(LocalDateTime.now());
        certificate.setExpiresAt(LocalDateTime.now().plusDays(365));
        certificate.setCertificateType("UPLOADED");
        certificate.setIsActive(true);

        certificateRepo.save(certificate);
    }
}