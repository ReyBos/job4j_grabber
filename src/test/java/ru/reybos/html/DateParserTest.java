package ru.reybos.html;

import org.junit.Test;
import ru.reybos.grabber.DateParser;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.junit.Assert.*;

public class DateParserTest {
    private DateParser dateParser = new DateParser();

    @Test
    public void whenParseToday() {
        Calendar out = dateParser.parseDate("сегодня, 22:00");
        Calendar expected = new GregorianCalendar(new Locale("ru"));
        expected.set(Calendar.HOUR, 22);
        expected.set(Calendar.MINUTE, 0);
        assertTrue(Math.abs(expected.getTimeInMillis() - out.getTimeInMillis()) < 1000);
    }

    @Test
    public void whenParseYesterday() {
        Calendar out = dateParser.parseDate("вчера, 22:00");
        Calendar expected = new GregorianCalendar(new Locale("ru"));
        expected.set(Calendar.HOUR, 22);
        expected.set(Calendar.MINUTE, 0);
        expected.add(Calendar.DAY_OF_MONTH, -1);
        assertTrue(Math.abs(expected.getTimeInMillis() - out.getTimeInMillis()) < 1000);
    }

    @Test
    public void whenParseJanuary() {
        Calendar out = dateParser.parseDate("2 янв 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.JANUARY, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }

    @Test
    public void whenParseFebruary() {
        Calendar out = dateParser.parseDate("2 фев 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.FEBRUARY, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }

    @Test
    public void whenParseMarch() {
        Calendar out = dateParser.parseDate("2 мар 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.MARCH, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }

    @Test
    public void whenParseApril() {
        Calendar out = dateParser.parseDate("2 апр 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.APRIL, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }

    @Test
    public void whenParseMay() {
        Calendar out = dateParser.parseDate("2 май 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.MAY, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }

    @Test
    public void whenParseJune() {
        Calendar out = dateParser.parseDate("2 июн 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.JUNE, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }

    @Test
    public void whenParseJuly() {
        Calendar out = dateParser.parseDate("2 июл 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.JULY, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }

    @Test
    public void whenParseAugust() {
        Calendar out = dateParser.parseDate("2 авг 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.AUGUST, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }

    @Test
    public void whenParseSeptember() {
        Calendar out = dateParser.parseDate("2 сен 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.SEPTEMBER, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }

    @Test
    public void whenParseOctober() {
        Calendar out = dateParser.parseDate("2 окт 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.OCTOBER, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }

    @Test
    public void whenParseNovember() {
        Calendar out = dateParser.parseDate("2 ноя 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.NOVEMBER, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }

    @Test
    public void whenParseDecember() {
        Calendar out = dateParser.parseDate("2 дек 19, 22:00");
        Calendar expected = new GregorianCalendar(2019, Calendar.DECEMBER, 2, 22, 0);
        assertEquals(expected.getTimeInMillis(), out.getTimeInMillis());
    }
}