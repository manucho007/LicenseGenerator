package ru.rtksoftlabs.licensegenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class LicenseGeneratorController {
    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    @Autowired
    private LicenseService licenseService;

    @GetMapping("/api/protected-objects")
    public List<ProtectedObject> getProtectedObjects() {
        return protectedObjectsService.getProtectedObjects();
    }

    @PostMapping("/api/generate-license")
    public ResponseEntity<byte[]> generateLicense(@RequestBody License license) throws IOException {
        return download(licenseService.generateLicense(license));
    }

    private ResponseEntity<byte[]> download(byte[] resource) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + licenseService.getLicenseFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
}
