package ru.rtksoftlabs.licensegenerator;

import java.time.LocalDate;
import java.util.ArrayList;

public class License {
    private LocalDate beginDate;
    private LocalDate endDate;

    private ArrayList<ProtectedObject> protectedObjects;

    public License() {
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ArrayList<ProtectedObject> getProtectedObjects() {
        return protectedObjects;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setProtectedObjects(ArrayList<ProtectedObject> protectedObjects) {
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
