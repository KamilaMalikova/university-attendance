package com.university;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Посещаемость пользователя
 */
public class UserAttendance implements Serializable {
    private final int univId;
    private final int userId;
    private final Date date;
    private final boolean isIn;
    private List<Instant> inHours = new ArrayList<>();
    private List<Instant> outHours = new ArrayList<>();

    public UserAttendance(int univId, int userId, Date date, boolean isIn) {
        this.univId = univId;
        this.userId = userId;
        this.date = date;
        this.isIn = isIn;
        if (isIn){
            inHours.add(date.toInstant());
        } else {
            outHours.add(date.toInstant());
        }
    }

    public int getUnivId() {
        return univId;
    }

    public int getUserId() {
        return userId;
    }

    public Instant getDate() {
        return date.toInstant();
    }

    public boolean isIn() {
        return isIn;
    }

    public void addToIn(Instant time){
        this.inHours.add(time);
    }

    public void addToOut(Instant time){
        this.outHours.add(time);
    }

    public long getHours(){
        Collections.sort(inHours);
        Collections.sort(outHours);
        long hours = 0;
        for (int i = 0; i < Math.min(inHours.size(), outHours.size()); i++) {
            long out = outHours.get(i).getEpochSecond()/(3600);
            long in = inHours.get(i).getEpochSecond()/(3600);
            hours+=out-in;
        }
        return hours;
    }
}
