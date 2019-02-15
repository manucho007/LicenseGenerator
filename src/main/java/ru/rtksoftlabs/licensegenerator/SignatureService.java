package ru.rtksoftlabs.licensegenerator;

import java.io.IOException;
import java.security.*;

public interface SignatureService {
    KeyPair generateKeyPair() throws NoSuchAlgorithmException;
    byte[] sign(byte[] messageBytes, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException;
    boolean verify(byte[] messageBytes, byte[] signatureBytes, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException;
    Keys loadOrCreateKeyStore() throws GeneralSecurityException, IOException;
}
