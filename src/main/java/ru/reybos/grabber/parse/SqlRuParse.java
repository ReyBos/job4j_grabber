package ru.reybos.grabber.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.reybos.grabber.store.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlRuParse implements Parse {
    public static DateParser dateParser = new DateParser();
    public static final Pattern DATE_TIME_PATTERN = Pattern.compile(".*, \\d{2}:\\d{2}");

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> posts = new ArrayList<>();
        Document doc = Jsoup.connect(link).get();
        Elements row = doc.select(".postslisttopic");
        for (int i = 0; i < row.size(); i++) {
            Element href = row.get(i).child(0);
            posts.add(detail(href.attr("href")));
        }
        return posts;
    }

    @Override
    public Post detail(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        Element msgBody = doc.select(".msgBody").get(1);
        Element msgFooter = doc.selectFirst(".msgFooter");
        Matcher footerMatcher = DATE_TIME_PATTERN.matcher(msgFooter.text());
        if (!footerMatcher.find()) {
            throw new IllegalStateException("Невозможно разобрать данные поста");
        }
        Element msgHeader = doc.selectFirst(".messageHeader");
        String title = msgHeader.text().replace("[new]", "").trim();
        return new Post(
                link,
                title,
                dateParser.parseDate(footerMatcher.group()),
                msgBody.text()
        );
    }
}
