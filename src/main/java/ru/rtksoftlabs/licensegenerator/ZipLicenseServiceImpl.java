package ru.rtksoftlabs.licensegenerator;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class ZipLicenseServiceImpl implements ZipLicenseService {
    @Override
    public void zipLicense(SignedLicenseContainer signedLicenseContainer) throws IOException {
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

        signedLicenseContainer.setZip(zipBytes);
    }

    @Override
    public void unzipLicense(SignedLicenseContainer signedLicenseContainer) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(signedLicenseContainer.getZip());
        ZipInputStream zis = new ZipInputStream(bais);

        ZipEntry entry;
        String name;

        while ((entry = zis.getNextEntry()) != null) {
            name = entry.getName();

            if (name.endsWith(".lic")) {
                signedLicenseContainer.setLicense(zis.readAllBytes());
            }
            else if (name.endsWith(".sign")) {
                signedLicenseContainer.setSign(zis.readAllBytes());
            }

            zis.closeEntry();
        }

        bais.close();

        zis.close();
    }
}
