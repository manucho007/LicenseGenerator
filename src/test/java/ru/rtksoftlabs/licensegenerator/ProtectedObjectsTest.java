package ru.rtksoftlabs.licensegenerator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import ru.rtksoftlabs.licensegenerator.shared.ProtectedObject;
import ru.rtksoftlabs.licensegenerator.shared.ProtectedObjects;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ProtectedObjectsTest {
    private ObjectMapper getJsonMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return mapper;
    }

    private String generateJson(ProtectedObjects protectedObjects) throws JsonProcessingException {
        return getJsonMapper().writeValueAsString(protectedObjects);
    }

    @Test
    public void addChilds() throws JsonProcessingException {
        ProtectedObjects protectedObjects = new ProtectedObjects();

        ProtectedObject protectedObject1 = protectedObjects.add("App1");
        protectedObjects.add("App2");

        ProtectedObject child = protectedObject1.addChild("Scripts");

        child.addChild("sc1");
        child.addChild("sc2");
        child.addChild("sc3");

        protectedObject1.addChild("Roles");

        String content = generateJson(protectedObjects);

        String expectedString = "{\"objects\":{\"App2\":{\"data\":\"App2\"},\"App1\":{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc1\"},{\"data\":\"sc2\"},{\"data\":\"sc3\"}]},{\"data\":\"Roles\"}]}}}";

        assertThat(content).isEqualTo(expectedString);
    }

    @Test
    public void whenAddSameAppThenAddingOnlyChilds() throws JsonProcessingException {
        ProtectedObjects protectedObjects = new ProtectedObjects();

        ProtectedObject protectedObject1 = protectedObjects.add("App1").addChild("Scripts");

        protectedObject1.addChild("sc1");
        protectedObject1.addChild("sc2");
        protectedObject1.addChild("sc3");

        ProtectedObject protectedObject2 = protectedObjects.add("App1").addChild("Roles");

        protectedObject2.addChild("role1");
        protectedObject2.addChild("role2");
        protectedObject2.addChild("role3");

        String content = generateJson(protectedObjects);

        String expectedString = "{\"objects\":{\"App1\":{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc1\"},{\"data\":\"sc2\"},{\"data\":\"sc3\"}]},{\"data\":\"Roles\",\"children\":[{\"data\":\"role1\"},{\"data\":\"role2\"},{\"data\":\"role3\"}]}]}}}";

        assertThat(content).isEqualTo(expectedString);
    }

    @Test
    public void whenAddProtectedObjectThenAddingAsChild() throws JsonProcessingException {
        ProtectedObjects protectedObjects = new ProtectedObjects();

        ProtectedObject protectedObject1 = new ProtectedObject("App1");

        protectedObject1.addChild("Roles").addChild("role1");

        protectedObjects.add(protectedObject1);

        ProtectedObject protectedObject2 = new ProtectedObject("Scripts");

        protectedObject2.addChild("sc1");

        protectedObject1.addChild(protectedObject2);

        String content = generateJson(protectedObjects);

        String expectedString = "{\"objects\":{\"App1\":{\"data\":\"App1\",\"children\":[{\"data\":\"Roles\",\"children\":[{\"data\":\"role1\"}]},{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc1\"}]}]}}}";

        assertThat(content).isEqualTo(expectedString);
    }

    @Test
    public void whenAddSameChildInBeginningThenAutoClapping() throws JsonProcessingException {
        ProtectedObjects protectedObjects = new ProtectedObjects();

        ProtectedObject protectedObject1 = new ProtectedObject("App1");

        protectedObject1.addChild("Scripts").addChild("sc1");

        protectedObject1.addChild("Scripts").addChild("sc2");

        protectedObjects.add(protectedObject1);

        String content = generateJson(protectedObjects);

        String expectedString = "{\"objects\":{\"App1\":{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc1\"},{\"data\":\"sc2\"}]}]}}}";

        assertThat(content).isEqualTo(expectedString);
    }

    @Test
    public void whenAddSameChildInMiddleThenAutoClapping() throws JsonProcessingException {
        ProtectedObjects protectedObjects = new ProtectedObjects();

        ProtectedObject protectedObject1 = new ProtectedObject("App1");

        protectedObject1.addChild("Scripts").addChild("sc1").addChild("sc2");

        protectedObject1.addChild("Scripts").addChild("sc1").addChild("sc3");

        protectedObjects.add(protectedObject1);

        String content = generateJson(protectedObjects);

        String expectedString = "{\"objects\":{\"App1\":{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc1\",\"children\":[{\"data\":\"sc2\"},{\"data\":\"sc3\"}]}]}]}}}";

        assertThat(content).isEqualTo(expectedString);
    }

    @Test
    public void whenAddSameChildObjectInMiddleThenAutoClapping() throws JsonProcessingException {
        ProtectedObjects protectedObjects = new ProtectedObjects();

        ProtectedObject protectedObject1 = new ProtectedObject("App1");
        ProtectedObject protectedObject2 = new ProtectedObject("App1");

        protectedObject1.addChild("Scripts").addChild("sc1").addChild("sc2");

        protectedObject2.addChild("Scripts").addChild("sc1").addChild("sc3");

        protectedObjects.add(protectedObject1);
        protectedObjects.add(protectedObject2);

        String content = generateJson(protectedObjects);

        String expectedString = "{\"objects\":{\"App1\":{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc1\",\"children\":[{\"data\":\"sc2\"},{\"data\":\"sc3\"}]}]}]}}}";

        assertThat(content).isEqualTo(expectedString);
    }

    @Test
    public void whenAddSameChildObjectInEndThenAutoClapping() throws JsonProcessingException {
        ProtectedObjects protectedObjects = new ProtectedObjects();

        ProtectedObject protectedObject1 = new ProtectedObject("App1");
        ProtectedObject protectedObject2 = new ProtectedObject("App1");

        protectedObject1.addChild("Scripts").addChild("sc2").addChild("sc5");

        protectedObject2.addChild("Scripts").addChild("sc1").addChild("sc5");

        protectedObjects.add(protectedObject1);
        protectedObjects.add(protectedObject2);

        String content = generateJson(protectedObjects);

        String expectedString = "{\"objects\":{\"App1\":{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc2\",\"children\":[{\"data\":\"sc5\"}]},{\"data\":\"sc1\",\"children\":[{\"data\":\"sc5\"}]}]}]}}}";

        assertThat(content).isEqualTo(expectedString);
    }

    @Test
    public void whenAddDifferentObjectsThenAllowRepeatsInChilds() throws JsonProcessingException {
        ProtectedObjects protectedObjects = new ProtectedObjects();

        ProtectedObject protectedObject1 = new ProtectedObject("App1");
        ProtectedObject protectedObject2 = new ProtectedObject("App2");

        protectedObject1.addChild("Scripts").addChild("sc1").addChild("sc2");

        protectedObject2.addChild("Scripts").addChild("sc1").addChild("sc2");

        protectedObjects.add(protectedObject1);
        protectedObjects.add(protectedObject2);

        String content = generateJson(protectedObjects);

        String expectedString = "{\"objects\":{\"App2\":{\"data\":\"App2\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc1\",\"children\":[{\"data\":\"sc2\"}]}]}]},\"App1\":{\"data\":\"App1\",\"children\":[{\"data\":\"Scripts\",\"children\":[{\"data\":\"sc1\",\"children\":[{\"data\":\"sc2\"}]}]}]}}}";

        assertThat(content).isEqualTo(expectedString);
    }
}
