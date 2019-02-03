package ru.rtksoftlabs.licensegenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class LicenseGeneratorController {
    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    @GetMapping("/api/protected-objects")
    public List<ProtectedObject> getProtectedObjects() {
        return protectedObjectsService.getProtectedObjects();
    }

    @PostMapping("/api/generate-license")
    public ResponseEntity<Resource> generateLicense(@RequestBody License license) throws IOException {
        //{"beginDate":"2018-12-27", "endDate":"2018-12-29", "protectedObjects":[{"name":"App1","components":["Component1","Component2","Component3"]},{"name":"App2","components":["Component1","Component2","Component3"]}]}

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS");

        String licenseFileName = "license_" + LocalDateTime.now().format(formatter) + ".lic";

        ByteArrayResource resource = generateAndSaveLicenseFile(license, licenseFileName);

        return download(resource, licenseFileName);
    }

    private ByteArrayResource generateAndSaveLicenseFile(License license, String licenseFileName) throws IOException {
        String licenseFile = license.toString();

        byte[] licenseFileInBytes = licenseFile.getBytes();

        Path path = Paths.get(licenseFileName);
        Files.write(path, licenseFileInBytes);

        return new ByteArrayResource(licenseFileInBytes);
    }

    private ResponseEntity<Resource> download(ByteArrayResource resource, String licenseFileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + licenseFileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
}
