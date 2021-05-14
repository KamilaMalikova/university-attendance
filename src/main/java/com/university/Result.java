package com.university;

import java.io.Serializable;

//  Выходные данные: год; идентификатор сотрудника/студента; количество публикаций за год; общее время, проведенное в организации в часах за год.
public class Result implements Serializable {
    private final int year;
    private final long univId;
    private final long userId;
    private final long publications;
    private final long hourInUniv;

    public Result(int year, long univId, long userId, long publications, long hourInUniv) {
        this.year = year;
        this.univId = univId;
        this.userId = userId;
        this.publications = publications;
        this.hourInUniv = hourInUniv;
    }

    public int getYear() {
        return year;
    }

    public long getUnivId() {
        return univId;
    }

    public long getUserId() {
        return userId;
    }

    public long getPublications() {
        return publications;
    }

    public long getHourInUniv() {
        return hourInUniv;
    }
}
