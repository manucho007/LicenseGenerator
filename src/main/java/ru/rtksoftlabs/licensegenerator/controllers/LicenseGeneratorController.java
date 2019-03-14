package ru.rtksoftlabs.licensegenerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.rtksoftlabs.licensegenerator.services.LicenseService;
import ru.rtksoftlabs.licensegenerator.shared.ProtectedObject;
import ru.rtksoftlabs.licensegenerator.services.ProtectedObjectsService;
import ru.rtksoftlabs.licensegenerator.util.SignedLicenseContainer;
import ru.rtksoftlabs.licensegenerator.util.License;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LicenseGeneratorController {
    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    @Autowired
    private LicenseService licenseService;

    @GetMapping("/protected-objects")
    public List<ProtectedObject> getProtectedObjects() {
        return protectedObjectsService.getProtectedObjects();
    }

    @PutMapping("/update-protected-objects")
    public ResponseEntity<?> updateProtectedObjectsList() {
        protectedObjectsService.updateProtectedObjects();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate-license")
    public ResponseEntity<byte[]> generateLicense(@RequestBody License license) throws IOException {
        SignedLicenseContainer signedLicenseContainer = licenseService.generateLicense(license);

        return download(signedLicenseContainer);
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
