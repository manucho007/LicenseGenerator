package ru.rtksoftlabs.licensegenerator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.rtksoftlabs.licensegenerator.util.SignedLicenseContainer;
import ru.rtksoftlabs.licensegenerator.util.Keys;
import ru.rtksoftlabs.licensegenerator.util.License;

import java.io.*;
import java.security.*;

@Service
public class LicenseServiceImpl implements LicenseService {
    @Value("${license.writetofile}")
    private boolean isLicenseWriteToFile;

    @Value("${signature.writetofile}")
    private boolean isSignatureWriteToFile;

    @Value("${zip.writetofile}")
    private boolean isZipWriteToFile;

    @Autowired
    private SignatureService signatureService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ZipLicenseService zipLicenseService;

    private byte[] signLicense(SignedLicenseContainer signedLicenseContainer) {
        try {
            Keys keys = signatureService.loadOrCreateKeyStore();

            byte[] signatureBytes = signatureService.sign(signedLicenseContainer.getLicense(), keys.getPrivateKey());

            if (isSignatureWriteToFile) {
                fileService.save(signatureBytes, signedLicenseContainer.getSignFileName());
            }

            return signatureBytes;
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public SignedLicenseContainer getNewSignedLicenseContainer() {
        return new SignedLicenseContainer();
    }

    @Override
    public SignedLicenseContainer generateLicense(License license) {
        try {
            SignedLicenseContainer signedLicenseContainer = getNewSignedLicenseContainer();

            byte[] licenseBytes = license.toJson().getBytes();

            signedLicenseContainer.setLicense(licenseBytes);

            if (isLicenseWriteToFile) {
                fileService.save(licenseBytes, signedLicenseContainer.getLicenseFileName());
            }

            byte[] signBytes = signLicense(signedLicenseContainer);

            signedLicenseContainer.setSign(signBytes);

            zipLicenseService.zipLicense(signedLicenseContainer);

            if (isZipWriteToFile) {
                fileService.save(signedLicenseContainer.getZip(), signedLicenseContainer.getZipFileName());
            }

            return signedLicenseContainer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
