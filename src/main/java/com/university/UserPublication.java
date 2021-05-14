package com.university;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.time.Instant;
import java.util.Set;

/**
 * Публикации пользователя
 */
public class UserPublication implements Serializable {
    private final int univId;
    private final int userId;
    private final int publicationId;
    private final Date date;
    private final Set<Integer> publicationsList = new HashSet<>();

    public UserPublication(int univId, int userId, int publicationId, Date date) {
        this.univId = univId;
        this.userId = userId;
        this.publicationId = publicationId;
        this.date = date;
        this.publicationsList.add(publicationId);
    }

    public int getUnivId() {
        return univId;
    }

    public int getUserId() {
        return userId;
    }

    public int getPublicationId() {
        return publicationId;
    }

    public Instant getDate() {
        return date.toInstant();
    }

    public long getPublicationsNumber() {
        return publicationsList.size();
    }

    public void addPublication(int num){
        publicationsList.add(num);
    }
}
