package ru.rtksoftlabs.licensegenerator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.rtksoftlabs.licensegenerator.util.Keys;
import sun.security.x509.*;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class SignatureServiceImpl implements SignatureService {
    @Value("${key.certificate.name}")
    private String keyCertificateName;

    @Value("${key.store.name}")
    private String keyStoreName;

    @Value("${key.alias.name}")
    private String keyAliasName;

    @Value("${key.store.password}")
    private String keyStorePassword;

    @Value("${key.password}")
    private String keyPassword;

    @Value("${key.certificate.type}")
    private String keyCertificateType;

    @Value("${key.pair.generator.type}")
    private String keyPairGeneratorType;

    @Value("${key.alg.name}")
    private String keyAlgName;

    @Value("${key.certificate.cn}")
    private String keyCertificateCN;

    @Value("${key.size}")
    private int keySize;

    @Value("${key.certificate.validity.days}")
    private int keyCertificateValidityDays;

    @Autowired
    private FileService fileService;

    @Override
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyPairGeneratorType);
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    @Override
    public byte[] sign(byte[] messageBytes, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(keyAlgName);

        signature.initSign(privateKey);

        signature.update(messageBytes);

        return signature.sign();
    }

    private Signature createSignature() throws NoSuchAlgorithmException {
        return Signature.getInstance(keyAlgName);
    }

    private boolean verify(Signature signature, byte[] messageBytes, byte[] signatureBytes) throws SignatureException {
        signature.update(messageBytes);

        return signature.verify(signatureBytes);
    }

    @Override
    public boolean verify(byte[] messageBytes, byte[] signatureBytes, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = createSignature();

        signature.initVerify(publicKey);

        return verify(signature, messageBytes, signatureBytes);
    }

    @Override
    public boolean verify(byte[] messageBytes, byte[] signatureBytes, Certificate certificate) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = createSignature();

        signature.initVerify(certificate);

        return verify(signature, messageBytes, signatureBytes);
    }

    public X509Certificate generateCertificate(String dn, KeyPair keyPair, int validity, String sigAlgName) throws GeneralSecurityException, IOException {
        PrivateKey privateKey = keyPair.getPrivate();

        X509CertInfo info = new X509CertInfo();

        Date from = new Date();
        Date to = new Date(from.getTime() + validity * 1000L * 24L * 60L * 60L);

        CertificateValidity interval = new CertificateValidity(from, to);
        BigInteger serialNumber = new BigInteger(64, new SecureRandom());
        X500Name owner = new X500Name(dn);
        AlgorithmId sigAlgId = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);

        info.set(X509CertInfo.VALIDITY, interval);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serialNumber));
        info.set(X509CertInfo.SUBJECT, owner);
        info.set(X509CertInfo.ISSUER, owner);
        info.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(sigAlgId));

        X509CertImpl certificate = new X509CertImpl(info);
        certificate.sign(privateKey, sigAlgName);

        sigAlgId = (AlgorithmId) certificate.get(X509CertImpl.SIG_ALG);
        info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, sigAlgId);
        certificate = new X509CertImpl(info);
        certificate.sign(privateKey, sigAlgName);

        return certificate;
    }

    public Certificate loadCertificate() throws IOException, CertificateException {
        FileInputStream inStream = fileService.loadInputStream(keyCertificateName);
        CertificateFactory cf = CertificateFactory.getInstance(keyCertificateType);

        return cf.generateCertificate(inStream);
    }

    private Keys loadKeyStore(String keystoreFileName) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        try {
            Certificate cert;
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream fis = fileService.loadInputStream(keystoreFileName);
            keyStore.load(fis, keyStorePassword.toCharArray());

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAliasName, keyPassword.toCharArray());

            if (Files.exists(Paths.get(keyCertificateName))) {
                FileInputStream inStream = fileService.loadInputStream(keyCertificateName);
                CertificateFactory cf = CertificateFactory.getInstance(keyCertificateType);
                cert = cf.generateCertificate(inStream);
            } else {
                cert = keyStore.getCertificate(keyAliasName);
                fileService.save(cert.getEncoded(), keyCertificateName);
            }

            return new Keys(privateKey, cert);
        }
        catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public KeyStore getKeyStoreWithCertificate(KeyPair keyPair) throws GeneralSecurityException, IOException {
        Certificate[] chain = {generateCertificate("cn=" + keyCertificateCN, keyPair, keyCertificateValidityDays, keyAlgName)};

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setKeyEntry(keyAliasName, keyPair.getPrivate(), keyPassword.toCharArray(), chain);

        return keyStore;
    }

    private Keys createKeyStore(String keystoreFileName) throws GeneralSecurityException, IOException {
        try {
            Certificate cert;

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyPairGeneratorType);
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            KeyStore keyStore = getKeyStoreWithCertificate(keyPair);

            cert = keyStore.getCertificate(keyAliasName);

            byte[] certBytes = cert.getEncoded();

            fileService.save(certBytes, keyCertificateName);

            FileOutputStream fos = fileService.saveOutputStream(keystoreFileName);

            keyStore.store(fos, keyStorePassword.toCharArray());

            return new Keys(privateKey, cert);
        }
        catch (IOException | GeneralSecurityException e) {
            throw e;
        }
    }

    public Keys loadOrCreateKeyStore() throws GeneralSecurityException, IOException {
        if (Files.notExists(Paths.get(keyStoreName))) {
            return createKeyStore(keyStoreName);
        }
        else {
            return loadKeyStore(keyStoreName);
        }
    }

    public String getKeyAliasName() {
        return keyAliasName;
    }
}
