package ru.rtksoftlabs.licensegenerator;

import io.netty.channel.ConnectTimeoutException;
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
import ru.rtksoftlabs.LicenseCommons.services.JsonMapperService;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObjects;
import ru.rtksoftlabs.licensegenerator.dao.ProtectedObjectsData;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("inno")
public class ProtectedObjectsDataTest {
    @SpyBean
    private ProtectedObjectsData protectedObjectsData;

    @Autowired
    private JsonMapperService jsonMapperService;

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
        return "{\"objects\":{\"TestApplication\":{\"data\":\"TestApplication\"},\"App" + appId + "\":{\"data\":\"App" + appId + "\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc1\"},{\"data\":\"sc2\"},{\"data\":\"sc3\"}]},{\"data\":\"Roles\"}]}}}";
    }

    private void PrepareResponses(int count) throws IOException {
        int port = 8191;

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
        Map<String, Mono<ProtectedObjects>> monosList = protectedObjectsData.getRequestList();

        monosList = new TreeMap<>(monosList);

        PrepareResponses(monosList.size());

        assertThat(protectedObjectsData.getProtectedObjects()).size().isEqualTo(0);

        int i = 0;

        List<ProtectedObjects> preparedContentList = new ArrayList<>();

        AtomicInteger sizeOfSuccessRequests = new AtomicInteger(monosList.size());

        for (Map.Entry<String, Mono<ProtectedObjects>> result: monosList.entrySet()) {
            String preparedContent = prepareContent(i++);

            ProtectedObjects expectedContent = jsonMapperService.generateProtectedObjects(preparedContent);

            preparedContentList.add(expectedContent);

            if (result.getKey().equals("unavailable")) {
                StepVerifier.create(result.getValue())
                        .expectErrorSatisfies(throwable -> {
                                assertThat(throwable).isInstanceOf(ConnectTimeoutException.class);
                                assertThat(throwable.getMessage()).isEqualTo("connection timed out: /1.2.3.4:500");

                                sizeOfSuccessRequests.getAndDecrement();
                        })
                        .verify();
            }
            else {
                StepVerifier.create(result.getValue())
                        .consumeNextWith(
                                responseContent -> {
                                    assertThat(responseContent).isEqualToComparingFieldByField(expectedContent);

                                    protectedObjectsData.addToMap(result.getKey(), responseContent);
                                })
                        .expectComplete().verify();
            }
        }

        assertThat(protectedObjectsData.getProtectedObjects()).size().isEqualTo(sizeOfSuccessRequests.get());

        i = 0;

        Map<String, ProtectedObjects> protectedObjects = new TreeMap<>(protectedObjectsData.getProtectedObjects());

        for (Map.Entry<String, ProtectedObjects> entry: protectedObjects.entrySet()) {
            assertThat(entry.getValue()).isEqualToComparingFieldByField(preparedContentList.get(i++));
        }
    }
}
