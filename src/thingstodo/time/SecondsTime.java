package thingstodo.time;

public class SecondsTime {

    private int seconds;

    public SecondsTime(int seconds) {
        this.seconds = seconds;
    }

    public SecondsTime() {
        this(0);
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

}
