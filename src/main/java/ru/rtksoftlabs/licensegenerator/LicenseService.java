package ru.rtksoftlabs.licensegenerator;

import java.io.IOException;

public interface LicenseService {
    byte[] generateLicense(License license) throws IOException;
    String generateLicenseFileName();
    void save(byte[] license) throws IOException;
    String getLicenseFileName();
}
