package ru.rtksoftlabs.licensegenerator.services;

import ru.rtksoftlabs.licensegenerator.util.SignedLicenseContainer;
import ru.rtksoftlabs.licensegenerator.util.License;

public interface LicenseService {
    SignedLicenseContainer generateLicense(License license);
    SignedLicenseContainer getNewSignedLicenseContainer();
}
