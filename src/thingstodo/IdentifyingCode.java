package thingstodo;

import java.util.Random;

public class IdentifyingCode {

    public static char[] generate() {
        Random r = new Random();
        char[] charCode = new char[5];
        for (int i = 0; i < 4; i++) {
            charCode[i] = (char) ('0' + r.nextInt(0, 10));
        }
        charCode[4] = (char) ('A' + r.nextInt(0, 26));
        return charCode;
    }

    public static boolean isEqual(char[] input, char[] realCode) {
        String inputString = new String(input);
        String realCodeString = new String(realCode);
        if (inputString.length() != 5) {
            return false;
        } else {
            for (int i = 0; i < 4; i++) {
                if (!(inputString.charAt(i) == realCodeString.charAt(i))) {
                    return false;
                }
            }
            if (!(inputString.toUpperCase().charAt(4) == realCodeString.charAt(4))) {
                return false;
            } else {
                return true;
            }
        }
    }

}
