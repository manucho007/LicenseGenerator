package ru.rtksoftlabs.licensegenerator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inno")
public class LicenseGeneratorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProtectedObjectsService protectedObjectsService;

    @Test
    public void getProtectedObjectsShouldReturnObjects() throws Exception {
        String content = "[{\"name\":\"App1\",\"components\":[\"Component1\",\"Component2\",\"Component3\"]},{\"name\":\"App2\",\"components\":[\"Component1\",\"Component2\",\"Component3\"]}]";

        mockMvc.perform(get("/api/protected-objects")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(content));
    }

    @Test
    public void generateLicenseShouldDownloadLicenseFile() throws Exception {
        License license = new License();

        license.setBeginDate(LocalDate.parse("2018-12-27"));
        license.setEndDate(LocalDate.parse("2018-12-29"));

        license.setProtectedObjects(protectedObjectsService.getProtectedObjects());

        mockMvc.perform(post("/api/generate-license").content("{\"beginDate\":\"2018-12-27\", \"endDate\":\"2018-12-29\", \"protectedObjects\":[{\"name\":\"App1\",\"components\":[\"Component1\",\"Component2\",\"Component3\"]},{\"name\":\"App2\",\"components\":[\"Component1\",\"Component2\",\"Component3\"]}]}").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, allOf(startsWith("attachment; filename=license_"), endsWith(".lic"))))
                .andExpect(content().bytes(license.toString().getBytes()))
                .andExpect(status().isOk());
    }
}
