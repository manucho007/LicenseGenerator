package ru.rtksoftlabs.licensegenerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import ru.rtksoftlabs.LicenseCommons.services.ProtectedObjectsService;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObjects;
import ru.rtksoftlabs.LicenseCommons.util.License;
import ru.rtksoftlabs.LicenseCommons.util.SignedLicenseContainer;
import ru.rtksoftlabs.licensegenerator.services.LicenseService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LicenseGeneratorController {
    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    @Autowired
    private LicenseService licenseService;

    @GetMapping("/protected-objects")
    public ProtectedObjects getProtectedObjects() {
        return returnProtectedObjects();
    }

    @PostMapping("/view-license")
    public License viewLicense(MultipartHttpServletRequest request) throws IOException {
        for (Map.Entry<String, MultipartFile> elem : request.getFileMap().entrySet()) {
            if (elem.getValue().getOriginalFilename().endsWith(".zip")) {
                return licenseService.viewLicense(elem.getValue().getBytes());
            }
        }

        throw new RuntimeException("License view failed");
    }

    @PutMapping("/update-protected-objects")
    public ProtectedObjects updateProtectedObjectsList() {
        protectedObjectsService.updateProtectedObjects();

        return returnProtectedObjects();
    }

    @PostMapping("/generate-license")
    public ResponseEntity<byte[]> generateLicense(@RequestBody License license) {
        SignedLicenseContainer signedLicenseContainer = licenseService.generateLicense(license);

        return download(signedLicenseContainer);
    }

    private ProtectedObjects returnProtectedObjects() {
        return protectedObjectsService.getProtectedObjects();
    }

    private ResponseEntity<byte[]> download(SignedLicenseContainer signedLicenseContainer) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + signedLicenseContainer.getZipFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(signedLicenseContainer.getZip());
    }
}
