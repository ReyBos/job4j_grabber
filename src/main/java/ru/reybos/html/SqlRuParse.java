package ru.reybos.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.reybos.grabber.DateParser;
import ru.reybos.grabber.Post;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlRuParse {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
    public static DateParser dateParser = new DateParser();
    public static final Pattern DATE_PATTERN = Pattern.compile(".*, \\d{2}:\\d{2}");

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 5; i++) {
            parsePage(i);
        }
    }

    public static void parsePage(int page) throws IOException {
        String url = String.format("https://www.sql.ru/forum/job-offers/%d", page);
        Document doc = Jsoup.connect(url).get();
        Elements row = doc.select(".postslisttopic");
        Elements dateRow = doc.select("td[style].altCol");
        for (int i = 0; i < row.size(); i++) {
            Element href = row.get(i).child(0);
            System.out.println(parsePost(href));
//            System.out.println(href.attr("href"));
//            System.out.println(href.text());
//            Element date = dateRow.get(i);
//            System.out.println(date.text());
//            System.out.println(dateFormat.format(parser.parseDate(date.text()).getTime()));
        }
    }

    public static Post parsePost(Element href) throws IOException {
        Document doc = Jsoup.connect(href.attr("href")).get();
        Element msgBody = doc.select(".msgBody").get(1);
        Element msgFooter = doc.selectFirst(".msgFooter");
        Matcher footerMatcher = DATE_PATTERN.matcher(msgFooter.text());
        if (!footerMatcher.find()) {
            throw new IllegalStateException("Невозможно разобрать дату поста");
        }
        return new Post(
                href.attr("href"),
                href.text(),
                dateParser.parseDate(footerMatcher.group()),
                msgBody.text()
        );
    }
}