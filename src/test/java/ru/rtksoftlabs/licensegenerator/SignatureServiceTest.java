package ru.rtksoftlabs.licensegenerator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SignatureServiceTest {
    @Autowired
    private SignatureService signatureService;

    @MockBean
    FileService fileService;

    @Test
    public void verifyShouldReturnTrueWhenSignedMessageWasNotModifiedAfterSign() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        String message = "Hello";

        KeyPair keyPair = signatureService.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();

        byte[] signatureBytes = signatureService.sign(message.getBytes(), privateKey);

        PublicKey publicKey = keyPair.getPublic();

        Assert.assertTrue(signatureService.verify(message.getBytes(), signatureBytes, publicKey));
    }

    @Test
    public void verifyShouldReturnFalseWhenSignedMessageWasModifiedAfterSign() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        String messageBeforeSign = "Hello";

        KeyPair keyPair = signatureService.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();

        byte[] signatureBytes = signatureService.sign(messageBeforeSign.getBytes(), privateKey);

        PublicKey publicKey = keyPair.getPublic();

        String messageAfterSign = "Hello!!!";

        Assert.assertFalse(signatureService.verify(messageAfterSign.getBytes(), signatureBytes, publicKey));
    }

    @Test
    public void verifyShouldReturnFalseWhenSignatureWasModifiedAfterSign() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        String message = "Hello";

        KeyPair keyPair = signatureService.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();

        byte[] signatureBytes = signatureService.sign(message.getBytes(), privateKey);

        signatureBytes[signatureBytes.length-1] = 'd';

        PublicKey publicKey = keyPair.getPublic();

        Assert.assertFalse(signatureService.verify(message.getBytes(), signatureBytes, publicKey));
    }

    @Test
    public void verifyShouldReturnTrueWhenPublicKeyWasEncodedAndDecoded() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException {
        String message = "Hello";

        KeyPair keyPair = signatureService.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();

        byte[] signatureBytes = signatureService.sign(message.getBytes(), privateKey);

        PublicKey publicKey = keyPair.getPublic();

        byte[] publicKeyBytes = publicKey.getEncoded();

        publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        Assert.assertTrue(signatureService.verify(message.getBytes(), signatureBytes, publicKey));
    }

    @Test
    public void verifyShouldReturnFalseWhenPublicKeyWasModified() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException {
        String message = "Hello";

        KeyPair keyPair = signatureService.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();

        byte[] signatureBytes = signatureService.sign(message.getBytes(), privateKey);

        PublicKey publicKey = keyPair.getPublic();

        byte[] publicKeyBytes = publicKey.getEncoded();

        publicKeyBytes[publicKeyBytes.length-1] = 'd';

        publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        Assert.assertFalse(signatureService.verify(message.getBytes(), signatureBytes, publicKey));
    }
}
