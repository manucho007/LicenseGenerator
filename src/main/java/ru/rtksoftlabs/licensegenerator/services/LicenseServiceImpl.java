package ru.rtksoftlabs.licensegenerator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.rtksoftlabs.LicenseCommons.services.FileService;
import ru.rtksoftlabs.LicenseCommons.services.JsonMapperService;
import ru.rtksoftlabs.LicenseCommons.services.SignatureService;
import ru.rtksoftlabs.LicenseCommons.services.ZipLicenseService;
import ru.rtksoftlabs.LicenseCommons.util.Keys;
import ru.rtksoftlabs.LicenseCommons.util.License;
import ru.rtksoftlabs.LicenseCommons.util.SignedLicenseContainer;
import ru.rtksoftlabs.licensegenerator.exceptions.GenerateLicenseException;
import ru.rtksoftlabs.licensegenerator.exceptions.SignLicenseException;

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

    @Autowired
    private JsonMapperService jsonMapperService;

    private byte[] signLicense(SignedLicenseContainer signedLicenseContainer) {
        try {
            Keys keys = signatureService.loadOrCreateKeyStore();

            byte[] signatureBytes = signatureService.sign(signedLicenseContainer.getLicense(), keys.getPrivateKey());

            if (isSignatureWriteToFile) {
                fileService.save(signatureBytes, signedLicenseContainer.getSignFileName());
            }

            return signatureBytes;
        } catch (IOException | GeneralSecurityException e) {
            throw new SignLicenseException("Signing license failed", e);
        }
    }

    public SignedLicenseContainer getNewSignedLicenseContainer() {
        return new SignedLicenseContainer();
    }

    @Override
    public SignedLicenseContainer generateLicense(License license) {
        try {
            SignedLicenseContainer signedLicenseContainer = getNewSignedLicenseContainer();

            byte[] licenseBytes = jsonMapperService.generateJson(license).getBytes();

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
            throw new GenerateLicenseException("Generating license failed", e);
        }
    }
}
