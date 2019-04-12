package ru.rtksoftlabs.licensegenerator.config;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import ru.rtksoftlabs.LicenseCommons.services.*;
import ru.rtksoftlabs.LicenseCommons.services.impl.FileServiceImpl;
import ru.rtksoftlabs.LicenseCommons.services.impl.JsonMapperServiceImpl;
import ru.rtksoftlabs.LicenseCommons.services.impl.SignatureServiceImpl;
import ru.rtksoftlabs.LicenseCommons.services.impl.ZipLicenseServiceImpl;
import ru.rtksoftlabs.licensegenerator.services.impl.ProtectedObjectsServiceImpl;

import java.util.concurrent.TimeUnit;

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

    @Value("${webclient.connection.timeout}")
    private int connectionTimeout;

    @Value("${webclient.read.timeout}")
    private int readTimeout;

    @Bean
    public WebClient webClient() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setDefaultSetterInfo(JsonSetter.Value.forContentNulls(Nulls.SKIP));

        ExchangeStrategies strategies = ExchangeStrategies
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON));

                }).build();

        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)));

        return WebClient.builder().exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }

    @Bean
    public JsonMapperService jsonMapperService() {
        return new JsonMapperServiceImpl();
    }

    @Bean
    @Profile("!inno")
    public ProtectedObjectsService protectedObjectsService() {
        return new ProtectedObjectsServiceImpl();
    }

    @Bean
    @Profile("inno")
    public ProtectedObjectsService protectedObjectsServiceInno() {
        return new ru.rtksoftlabs.LicenseCommons.inno.ProtectedObjectsServiceImpl();
    }

    @Bean
    public SignatureService signatureService() {
        SignatureServiceImpl signatureService = new SignatureServiceImpl();

        signatureService.setFileService(fileService());

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
