package ru.rtksoftlabs.licensegenerator.services;

import ru.rtksoftlabs.licensegenerator.util.SignedLicenseContainer;
import ru.rtksoftlabs.licensegenerator.util.License;

import java.io.IOException;

public interface LicenseService {
    SignedLicenseContainer generateLicense(License license) throws IOException;
    SignedLicenseContainer getNewSignedLicenseContainer();
}
