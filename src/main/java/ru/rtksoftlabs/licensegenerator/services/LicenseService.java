package ru.rtksoftlabs.licensegenerator.services;

import ru.rtksoftlabs.LicenseCommons.util.License;
import ru.rtksoftlabs.LicenseCommons.util.SignedLicenseContainer;

public interface LicenseService {
    SignedLicenseContainer generateLicense(License license);
    SignedLicenseContainer getNewSignedLicenseContainer();
}
