package ru.rtksoftlabs.licensegenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inno")
public class LicenseGeneratorControllerTest {
    @SpyBean
    private LicenseService licenseService;

    @SpyBean
    private SignatureService signatureService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    @MockBean
    private FileService fileService;

    private License license;

    @Before
    public void before() {
        license = new License();

        license.setBeginDate(LocalDate.parse("2018-12-27"));
        license.setEndDate(LocalDate.parse("2018-12-29"));

        license.setProtectedObjects(protectedObjectsService.getProtectedObjects());
    }

    @Test
    public void getProtectedObjectsShouldReturnObjects() throws Exception {
        List<ProtectedObject> protectedObjects = license.getProtectedObjects();

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String content = mapper.writeValueAsString(protectedObjects);

        mockMvc.perform(get("/api/protected-objects")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(content));
    }

    @Test
    public void generateLicenseShouldDownloadZipFile() throws Exception {
        SignedLicenseContainer signedLicenseContainerTest = new SignedLicenseContainer();

        Mockito.when(licenseService.getNewSignedLicenseContainer()).thenReturn(signedLicenseContainerTest);

        KeyPair keyPair = signatureService.generateKeyPair();

        KeyStore keyStoreWithCertificate = signatureService.getKeyStoreWithCertificate(keyPair);

        Certificate certificate = keyStoreWithCertificate.getCertificate(signatureService.getKeyAliasName());

        Keys keys = new Keys(keyPair.getPrivate(), certificate);

        Mockito.doReturn(keys).when(signatureService).loadOrCreateKeyStore();

        Mockito.verify(fileService, Mockito.never()).load(any(String.class));

        Mockito.verify(fileService, Mockito.never()).save(any(byte[].class), any(String.class));

        SignedLicenseContainer signedLicenseContainer = licenseService.generateLicense(license);

        mockMvc.perform(post("/api/generate-license").content(license.toJson()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, allOf(startsWith("attachment; filename=license_"), endsWith(".zip"))))
                .andExpect(content().bytes(signedLicenseContainer.getZip()))
                .andExpect(status().isOk());
    }
}
