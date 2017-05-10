package co.epitre.aelf_lectures.data;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * This class centralizes all helpers around dates like
 * - is it today ?
 * - is it this week ?
 * - is it tomorow ?
 * - is it ext sunday ?
 * - ...
 */

public class AelfDate extends GregorianCalendar {

    // Default constructor
    public AelfDate() {
        super();
    }

    // Date constructor
    public AelfDate(int year, int month, int day) {
        super(year, month, day);
    }

    //
    // Setter
    //

    public void setFromUrlDate(String date) throws IllegalArgumentException {
        if (date.matches("20[0-9]{2}-[0-9]{2}-[0-9]{2}")) {
            String[] date_chunks = date.split("-");
            set(
                    Integer.parseInt(date_chunks[0]),
                    Integer.parseInt(date_chunks[1]) - 1,
                    Integer.parseInt(date_chunks[2])
            );
        } else if (date.equals("today")) {
            GregorianCalendar today = new GregorianCalendar();
            set(
                    today.get(GregorianCalendar.YEAR),
                    today.get(GregorianCalendar.MONTH),
                    today.get(GregorianCalendar.DAY_OF_MONTH)
            );
        } else if (date.equals("sunday")) {
            GregorianCalendar sunday = new GregorianCalendar();
            int day = sunday.get(GregorianCalendar.DAY_OF_WEEK);
            int forward = (8 - day) % 7;
            sunday.add(GregorianCalendar.DAY_OF_YEAR, forward);
            set(
                    sunday.get(GregorianCalendar.YEAR),
                    sunday.get(GregorianCalendar.MONTH),
                    sunday.get(GregorianCalendar.DAY_OF_MONTH)
            );
        } else {
            throw new IllegalArgumentException("'"+date+"' is not a valid date");
        }
    }

    //
    // String formatting
    //

    private String internalPrettyString(String dayFormat, String monthFormat, boolean withDeterminant) {
        if (isToday()) {
            return withDeterminant ? "d'aujourd'hui" : "aujourd'hui";
        }

        if (isTomorrow() && !isSunday()) {
            return withDeterminant ? "de demain" : "demain";
        }

        if (isYesterday() && !isSunday()) {
            return withDeterminant ? "d'hier" : "hier";
        }

        if (isWithin7NextDays()) {
            String intro = withDeterminant ? "de " : "";
            return intro + new SimpleDateFormat(dayFormat).format(getTimeInMillis()) + " prochain";
        }

        if (isWithin7PrevDays()) {
            String intro = withDeterminant ? "de " : "";
            return intro + new SimpleDateFormat(dayFormat).format(getTimeInMillis()) + " dernier";
        }

        if (isSameYear(new GregorianCalendar())) {
            String intro = withDeterminant ? "du " : "";
            return intro + new SimpleDateFormat(dayFormat+" d "+monthFormat).format(getTimeInMillis());
        }

        // Long version: be explicit
        String intro = withDeterminant ? "du " : "";
        return intro + new SimpleDateFormat(dayFormat+" d "+monthFormat+" y").format(getTimeInMillis());
    }

    public String toPrettyString() {
        return internalPrettyString("EEEE", "MMMM", true);
    }

    public String toShortPrettyString() {
        return internalPrettyString("E", "MMM", false);
    }

    public String toIsoString() {
        return new SimpleDateFormat("yyyy-MM-dd").format(getTimeInMillis());
    }

    //
    // High level helpers
    //

    public boolean isToday() {
        GregorianCalendar today = new GregorianCalendar();
        return isSameDay(today);
    }

    public boolean isSunday() {
        return get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY;
    }

    public boolean isYesterday() {
        GregorianCalendar yesterday = new GregorianCalendar();
        yesterday.add(GregorianCalendar.DAY_OF_YEAR, -1);
        return isSameDay(yesterday);
    }

    public boolean isTomorrow() {
        GregorianCalendar tomorrow = new GregorianCalendar();
        tomorrow.add(GregorianCalendar.DAY_OF_YEAR, 1);
        return isSameDay(tomorrow);
    }

    public boolean isWithin7PrevDays() {
        GregorianCalendar today = new GregorianCalendar();
        GregorianCalendar prevWeek = new GregorianCalendar();
        prevWeek.add(GregorianCalendar.DAY_OF_YEAR, -8);
        return compareTo(today) < 0 && compareTo(prevWeek) >= 0;
    }

    public boolean isWithin7NextDays() {
        GregorianCalendar today = new GregorianCalendar();
        GregorianCalendar nextWeek = new GregorianCalendar();
        nextWeek.add(GregorianCalendar.DAY_OF_YEAR, 7);
        return compareTo(today) >= 0 && compareTo(nextWeek) < 0;
    }

    //
    // Low level helper
    //

    public boolean isSameYear(GregorianCalendar other) {
        return (get(GregorianCalendar.ERA) == other.get(GregorianCalendar.ERA) &&
                get(GregorianCalendar.YEAR) == other.get(GregorianCalendar.YEAR));
    }

    public boolean isSameDay(GregorianCalendar other) {
        return (isSameYear(other) && get(GregorianCalendar.DAY_OF_YEAR) == other.get(GregorianCalendar.DAY_OF_YEAR));
    }

    // Will return the number of days between self and other. If other is in the future, will be positive
    public long dayBetween(GregorianCalendar other) {
        return (getTimeInMillis() - other.getTimeInMillis()) / (1000 * 60 * 60 * 24);
    }
}
