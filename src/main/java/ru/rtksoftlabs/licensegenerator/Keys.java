package ru.rtksoftlabs.licensegenerator;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class Keys {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Certificate certificate;

    public Keys(PrivateKey privateKey, Certificate certificate) {
        this.privateKey = privateKey;
        this.certificate = certificate;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public Certificate getCertificate() {
        return certificate;
    }
}
