package ru.rtksoftlabs.licensegenerator;

import java.time.LocalDate;
import java.util.List;

public class License {
    private LocalDate beginDate;
    private LocalDate endDate;

    private List<ProtectedObject> protectedObjects;

    public License() {
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
}
