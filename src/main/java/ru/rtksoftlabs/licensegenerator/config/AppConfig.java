package ru.rtksoftlabs.licensegenerator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.rtksoftlabs.LicenseCommons.services.*;
import ru.rtksoftlabs.LicenseCommons.services.impl.FileServiceImpl;
import ru.rtksoftlabs.LicenseCommons.services.impl.JsonMapperServiceImpl;
import ru.rtksoftlabs.LicenseCommons.services.impl.SignatureServiceImpl;
import ru.rtksoftlabs.LicenseCommons.services.impl.ZipLicenseServiceImpl;
import ru.rtksoftlabs.licensegenerator.services.impl.ProtectedObjectsServiceImpl;

@Configuration
public class AppConfig {
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

    @Bean
    public JsonMapperService jsonMapperService() {
        return new JsonMapperServiceImpl();
    }

    @Bean
    public ProtectedObjectsService protectedObjectsService() {
        return new ProtectedObjectsServiceImpl();
    }

    @Bean
    public SignatureService signatureService() {
        SignatureServiceImpl signatureService = new SignatureServiceImpl();

        signatureService.setKeyCertificateName(keyCertificateName);
        signatureService.setKeyStoreName(keyStoreName);
        signatureService.setKeyAliasName(keyAliasName);
        signatureService.setKeyStorePassword(keyStorePassword);
        signatureService.setKeyPassword(keyPassword);
        signatureService.setKeyCertificateType(keyCertificateType);
        signatureService.setKeyPairGeneratorType(keyPairGeneratorType);
        signatureService.setKeyAlgName(keyAlgName);
        signatureService.setKeyCertificateCN(keyCertificateCN);
        signatureService.setKeySize(keySize);
        signatureService.setKeyCertificateValidityDays(keyCertificateValidityDays);

        return signatureService;
    }

    @Bean
    public FileService fileService() {
        return new FileServiceImpl();
    }

    @Bean
    public ZipLicenseService zipLicenseService() {
        return new ZipLicenseServiceImpl();
    }
}
