package ru.rtksoftlabs.licensegenerator.services;

import ru.rtksoftlabs.licensegenerator.util.SignedLicenseContainer;

import java.io.IOException;

public interface ZipLicenseService {
    void zipLicense(SignedLicenseContainer signedLicenseContainer) throws IOException;
    void unzipLicense(SignedLicenseContainer signedLicenseContainer) throws IOException;
}
