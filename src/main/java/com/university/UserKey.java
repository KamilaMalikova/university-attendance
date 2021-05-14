package com.university;

import java.io.Serializable;
import java.util.Objects;

public class UserKey implements Serializable {
    private final int univId;
    private final int userId;
    private final int year;

    public UserKey(int univId, int userId, int year) {
        this.univId = univId;
        this.userId = userId;
        this.year = year;
    }

    public int getUnivId() {
        return univId;
    }

    public int getUserId() {
        return userId;
    }

    public int getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserKey userKey = (UserKey) o;
        return univId == userKey.univId && userId == userKey.userId && year == userKey.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(univId, userId, year);
    }
}
