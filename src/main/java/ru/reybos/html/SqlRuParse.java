package ru.reybos.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;

public class SqlRuParse {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
    public static DateParser parser = new DateParser();

    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements row = doc.select(".postslisttopic");
        Elements dateRow = doc.select("td[style].altCol");
        for (int i = 0; i < row.size(); i++) {
            Element href = row.get(i).child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            Element date = dateRow.get(i);
            System.out.println(date.text());
            System.out.println(dateFormat.format(parser.parseDate(date.text()).getTime()));
        }
    }
}