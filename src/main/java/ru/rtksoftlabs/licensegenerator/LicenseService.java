package ru.rtksoftlabs.licensegenerator;

import java.io.IOException;

public interface LicenseService {
    SignedLicenseContainer generateLicense(License license) throws IOException;
    SignedLicenseContainer getNewSignedLicenseContainer();
}
