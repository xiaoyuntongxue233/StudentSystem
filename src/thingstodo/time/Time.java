package thingstodo.time;
import java.io.Serializable;

public class Time implements Serializable{

    private static final long serialVersionUID = 5L;
    
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public Time() {
        this(0, 0, 0);
    }

    public Time(int year, int month, int day, int hour, int minute) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.month = month;
        this.year = year;
    }

    public Time(int year, int month, int day) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Time(int year, int month) {
        this.month = month;
        this.year = year;
    }

    public Time(int year) {
        this.year = year;
    }

    public static boolean isLeapYear(int y) {
        return (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0);
    }

    public static int daysInMonth(int y, int m) {
        switch (m) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return isLeapYear(y) ? 29 : 28;
            default:
                return 0;
        }
    }

    public boolean isValid() {
        if (year < 0 || year > 10000)
            return false;
        if (month < 1 || month > 12)
            return false;
        if (day < 1 || day > daysInMonth(year, month))
            return false;
        if (hour < 0 || hour > 23)
            return false;
        if (minute < 0 || minute > 59)
            return false;
        return true;
    }

    public long toComparable() {
        return ((((long) year * 100 + month) * 100 + day) * 10000) + hour * 100 + minute;
    }

    public boolean isBefore(Time other) {
        if (other == null)
            return false;
        return this.toComparable() < other.toComparable();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
