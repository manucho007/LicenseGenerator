package ru.rtksoftlabs.licensegenerator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.rtksoftlabs.licensegenerator.config.ConfigUrlsForProtectedObjects;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("inno")
public class ProtectedObjectsDataTest {
    @SpyBean
    private ProtectedObjectsData protectedObjectsData;

    private List<MockWebServer> mockWebServerList;

    @Before
    public void beforeClass() {
        mockWebServerList = new ArrayList<>();
    }

    @After
    public void afterClass() throws IOException {
        for (MockWebServer mockWebServer: mockWebServerList) {
            mockWebServer.shutdown();
        }
    }

    private void prepareResponse(MockWebServer server, Consumer<MockResponse> consumer) {
        MockResponse response = new MockResponse();
        consumer.accept(response);
        server.enqueue(response);
    }

    private String prepareContent(int appId) {
        return "[{\"name\":\"App" + appId + "\",\"components\":{\"idComponent1\":\"nameComponent1\",\"idComponent3\":\"nameComponent3\",\"idComponent2\":\"nameComponent2\"}},{\"name\":\"App2\",\"components\":{\"idComponent1\":\"nameComponent1\",\"idComponent3\":\"nameComponent3\",\"idComponent2\":\"nameComponent2\"}}]";
    }

    private void PrepareResponses(int count) throws IOException {
        int port = 8083;

        for (int i = 0; i < count; i++) {
            String preparedContent = prepareContent(i);

            MockWebServer mockWebServer = new MockWebServer();

            mockWebServerList.add(mockWebServer);

            mockWebServer.start(port++);

            prepareResponse(mockWebServer, response -> response
                    .setHeader("Content-Type", "application/json")
                    .setResponseCode(200)
                    .setBody(preparedContent));

        }
    }

    @Test
    public void getRequestListTest() throws IOException {
        Map<String, Mono<List<ProtectedObject>>> monosList = protectedObjectsData.getRequestList();

        monosList = new TreeMap<>(monosList);

        PrepareResponses(monosList.size());

        assertThat(protectedObjectsData.getProtectedObjects()).size().isEqualTo(0);

        int i = 0;

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        List<List<ProtectedObject>> preparedContentList = new ArrayList<>();

        for (Map.Entry<String, Mono<List<ProtectedObject>>> result: monosList.entrySet()) {
            String preparedContent = prepareContent(i++);

            List<ProtectedObject> expectedContent = mapper.readValue(preparedContent, new TypeReference<List<ProtectedObject>>(){});

            preparedContentList.add(expectedContent);

            StepVerifier.create(result.getValue())
                    .consumeNextWith(
                            responseContent -> {
                                assertThat(responseContent).usingFieldByFieldElementComparator().isEqualTo(expectedContent);
                                protectedObjectsData.addToMap(result.getKey(), responseContent);
                            })
                    .expectComplete().verify();
        }

        assertThat(protectedObjectsData.getProtectedObjects()).size().isEqualTo(monosList.size());

        i = 0;

        Map<String, List<ProtectedObject>> protectedObjects = new TreeMap<>(protectedObjectsData.getProtectedObjects());

        for (Map.Entry<String, List<ProtectedObject>> entry: protectedObjects.entrySet()) {
            assertThat(entry.getValue()).usingFieldByFieldElementComparator().isEqualTo(preparedContentList.get(i++));
        }

        // TODO удалить! и брэйкпоинт тоже
        // Возвращаем другой контент к существующему ключу, чтобы проверить вызов add у map с защищаемыми объектами

        Map.Entry<String, Mono<List<ProtectedObject>>> entry = ((TreeMap<String, Mono<List<ProtectedObject>>>) monosList).lastEntry();

        Mono<List<ProtectedObject>> monoRequest = entry.getValue();

        String preparedContent = prepareContent(2);

        List<ProtectedObject> expectedContent = mapper.readValue(preparedContent, new TypeReference<List<ProtectedObject>>(){});

        prepareResponse(mockWebServerList.get(mockWebServerList.size() - 1), response -> response
                .setHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(preparedContent));

        StepVerifier.create(monoRequest)
                .consumeNextWith(
                        responseContent -> {
                            assertThat(responseContent).usingFieldByFieldElementComparator().isEqualTo(expectedContent);
                            protectedObjectsData.addToMap(entry.getKey(), responseContent);
                        })
                .expectComplete().verify();
    }
}
