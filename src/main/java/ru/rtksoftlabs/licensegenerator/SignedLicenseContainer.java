package ru.rtksoftlabs.licensegenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SignedLicenseContainer implements Iterable<Map.Entry<String, byte[]>> {
    private final Map<String, byte[]> fields = new HashMap<>();
    private byte[] license;
    private byte[] sign;
    private byte[] zip;
    private String fileName;
    private String licenseFileName;
    private String signFileName;
    private String zipFileName;

    public SignedLicenseContainer() {
        fileName = generateFileName();
        licenseFileName = generateLicenseFileName();
        signFileName = generateSignFileName();
        zipFileName = generateZipFileName();
    }

    public byte[] getLicense() {
        return license;
    }

    public void setLicense(byte[] license) {
        this.license = license;
    }

    public void setSign(byte[] sign) {
        this.sign = sign;
    }

    public void setZip(byte[] zip) {
        this.zip = zip;
    }

    public byte[] getZip() {
        return zip;
    }

    @Override
    public Iterator iterator() {
        fields.put(licenseFileName, license);
        fields.put(signFileName, sign);

        return fields.entrySet().iterator();
    }

    public String generateFileName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS");

        String fileName = "license_" + LocalDateTime.now().format(formatter);

        return fileName;
    }

    public String generateLicenseFileName() {
        return fileName + ".lic";
    }

    public String generateSignFileName() {
        return fileName + ".sign";
    }

    public String generateZipFileName() {
        return fileName + ".zip";
    }

    public String getLicenseFileName() {
        return licenseFileName;
    }

    public String getSignFileName() {
        return signFileName;
    }

    public String getZipFileName() {
        return zipFileName;
    }
}
