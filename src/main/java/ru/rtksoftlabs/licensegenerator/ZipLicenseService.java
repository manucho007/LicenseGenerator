package ru.rtksoftlabs.licensegenerator;

import java.io.IOException;

public interface ZipLicenseService {
    void zipLicense(SignedLicenseContainer signedLicenseContainer) throws IOException;
    void unzipLicense(SignedLicenseContainer signedLicenseContainer) throws IOException;
}
