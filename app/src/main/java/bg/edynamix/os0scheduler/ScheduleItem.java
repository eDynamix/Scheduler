package bg.edynamix.os0scheduler;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

public class ScheduleItem implements Serializable {
    private String title, description;
    private int year, month, day, hours, minutes;

    public ScheduleItem() {}
    public ScheduleItem(int year, int month, int day, int hours, int minutes) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hours = hours;
        this.minutes = minutes;
    }
    public ScheduleItem(String title, String description, int year, int month, int day, int hours, int minutes) {
        this(year, month, day, hours, minutes);
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getFormattedDate() {
        return String.format("%02d/%02d/%d @ %02d:%02d", day, month, year, hours, minutes);
    }
    public int getYear() {
        return year;
    }
    public int getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public int getHour() {
        return hours;
    }
    public int getMinute() {
        return minutes;
    }
    public Date getDate() {
        return new GregorianCalendar(year, month - 1, day, hours, minutes).getTime();
    }
}
