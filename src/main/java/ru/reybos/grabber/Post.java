package ru.reybos.grabber;

import java.util.Calendar;

public class Post {
    private Calendar date;
    private String description;

    public Post(Calendar date, String description) {
        this.date = date;
        this.description = description;
    }

    public Calendar getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
