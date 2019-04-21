package ru.rtksoftlabs.licensegenerator.services;

import ru.rtksoftlabs.LicenseCommons.util.License;
import ru.rtksoftlabs.LicenseCommons.util.SignedLicenseContainer;

import java.io.IOException;

public interface LicenseService {
    SignedLicenseContainer generateLicense(License license);
    License viewLicense(byte[] licenseBytes) throws IOException;
    SignedLicenseContainer getNewSignedLicenseContainer();
}
