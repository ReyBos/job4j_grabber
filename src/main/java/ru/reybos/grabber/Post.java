package ru.reybos.grabber;

import java.util.Calendar;

public class Post {
    private String link;
    private String title;
    private Calendar date;
    private String description;

    public Post(String link, String title, Calendar date, String description) {
        this.link = link;
        this.title = title;
        this.date = date;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Post{"
                + "link='" + link + '\''
                + ", title='" + title + '\''
                + ", date=" + date
                + ", description='" + description + '\''
                + '}';
    }
}
