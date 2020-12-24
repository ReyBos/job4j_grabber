package ru.reybos.grabber;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser {
    private LinkedHashMap<Function<String, Boolean>, Function<String, Calendar>> dispatch;
    private static final String TODAY = "сегодня";
    private static final String YESTERDAY = "вчера";
    private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}");
    private static final DateFormatSymbols DATE_FORMAT_SYMBOLS = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"янв", "фев", "мар", "апр", "май", "июн",
                    "июл", "авг", "сен", "окт", "ноя", "дек"};
        }
    };

    public DateParser() {
        dispatch = new LinkedHashMap<>();
        dispatch.put(
                s -> s.contains(TODAY),
                this::getTodayCalendar
        );
        dispatch.put(
                s -> s.contains(YESTERDAY),
                s -> {
                    Calendar today = this.getTodayCalendar(s);
                    today.add(Calendar.DAY_OF_MONTH, -1);
                    return today;
                }
        );
        dispatch.put(
                s -> s.length() > 0,
                this::getCalendar
        );
    }

    private Calendar getTodayCalendar(String str) {
        Matcher matcher = TIME_PATTERN.matcher(str);
        if (!matcher.find()) {
            throw new IllegalStateException("Невозможно разобрать время");
        }
        String[] time = matcher.group().split(":");
        Calendar today = new GregorianCalendar(new Locale("ru"));
        today.set(Calendar.HOUR, Integer.parseInt(time[0]));
        today.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        return today;
    }

    private Calendar getCalendar(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yy, HH:mm", new Locale("ru"));
        dateFormat.setDateFormatSymbols(DATE_FORMAT_SYMBOLS);
        Calendar calendar = new GregorianCalendar(new Locale("ru"));
        try {
            calendar.setTime(dateFormat.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public Calendar parseDate(String date) {
        for (Function<String, Boolean> predict : this.dispatch.keySet()) {
            if (predict.apply(date)) {
                return this.dispatch.get(predict).apply(date);
            }
        }
        throw new IllegalStateException("Невозможно разобрать дату");
    }
}
