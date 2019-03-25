package ru.rtksoftlabs.licensegenerator.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.rtksoftlabs.licensegenerator.shared.ProtectedObject;

import java.time.LocalDate;
import java.util.List;

public class License {
    private LocalDate beginDate;
    private LocalDate endDate;

    private List<ProtectedObject> protectedObjects;

    public License() {
        // That constructor needed for jackson mapping
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<ProtectedObject> getProtectedObjects() {
        return protectedObjects;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setProtectedObjects(List<ProtectedObject> protectedObjects) {
        this.protectedObjects = protectedObjects;
    }

    @Override
    public String toString() {
        return "License{" +
                "beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", protectedObjects=" + protectedObjects +
                '}';
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return mapper.writeValueAsString(this);
    }
}
