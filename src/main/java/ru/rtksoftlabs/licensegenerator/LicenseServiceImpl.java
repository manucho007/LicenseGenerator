package ru.rtksoftlabs.licensegenerator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LicenseServiceImpl implements LicenseService {
    private String licenseFileName;

    @Value("${license.writetofile}")
    private boolean isWriteToFile;

    public LicenseServiceImpl() {
        this.licenseFileName = generateLicenseFileName();
    }

    @Override
    public byte[] generateLicense(License license) throws IOException {
        byte[] licenseBytes = license.toString().getBytes();

        if (isWriteToFile) {
            save(licenseBytes);
        }

        return licenseBytes;
    }

    @Override
    public String generateLicenseFileName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS");

        String licenseFileName = "license_" + LocalDateTime.now().format(formatter) + ".lic";

        return licenseFileName;
    }

    @Override
    public void save(byte[] license) throws IOException {
        Path path = Paths.get(licenseFileName);
        Files.write(path, license);
    }

    @Override
    public String getLicenseFileName() {
        return licenseFileName;
    }
}
