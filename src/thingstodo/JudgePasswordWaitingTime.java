package thingstodo;

import java.lang.Math;

public class JudgePasswordWaitingTime {
    private int lockCount = 0;
    static int MAX_MINUTE = 120;

    public int getLockSeconds() {
        lockCount++;
        int minutes = (int) Math.min(Math.pow(2, lockCount), MAX_MINUTE);
        return minutes * 60;
    }

    public void reset() {
        lockCount = 0;
    }

}