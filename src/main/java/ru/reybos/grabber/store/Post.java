package ru.reybos.grabber.store;

import java.util.Calendar;

public class Post {
    private int id;
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

    public Post(int id, String link, String title, Calendar date, String description) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public Calendar getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }
}
