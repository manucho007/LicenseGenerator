package ru.rtksoftlabs.licensegenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;

import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    private byte[] signLicense(SignedLicenseContainer signedLicenseContainer) {
        try {
            Keys keys = signatureService.loadOrCreateKeyStore();

            byte[] signatureBytes = signatureService.sign(signedLicenseContainer.getLicense(), keys.getPrivateKey());

            if (isSignatureWriteToFile) {
                fileService.save(signatureBytes, signedLicenseContainer.getSignFileName());
            }

            return signatureBytes;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public SignedLicenseContainer getNewSignedLicenseContainer() {
        return new SignedLicenseContainer();
    }

    @Override
    public SignedLicenseContainer generateLicense(License license) {
        try {
            SignedLicenseContainer signedLicenseContainer = getNewSignedLicenseContainer();

            byte[] licenseBytes = license.toString().getBytes();

            signedLicenseContainer.setLicense(licenseBytes);

            if (isLicenseWriteToFile) {
                fileService.save(licenseBytes, signedLicenseContainer.getLicenseFileName());
            }

            byte[] signBytes = signLicense(signedLicenseContainer);

            signedLicenseContainer.setSign(signBytes);

            return zipLicense(signedLicenseContainer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private SignedLicenseContainer zipLicense(SignedLicenseContainer signedLicenseContainer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        for (Map.Entry<String, byte[]> elem : signedLicenseContainer) {
            ZipEntry entry = new ZipEntry(elem.getKey());
            entry.setSize(elem.getValue().length);
            zos.putNextEntry(entry);
            zos.write(elem.getValue());
        }

        zos.closeEntry();
        zos.close();

        byte[] zipBytes = baos.toByteArray();

        if (isZipWriteToFile) {
            fileService.save(zipBytes, signedLicenseContainer.getZipFileName());
        }

        signedLicenseContainer.setZip(zipBytes);

        return signedLicenseContainer;
    }
}
