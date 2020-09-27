package apple.questing.utils;

import java.text.NumberFormat;

public class Pretty {
    public static String commas(long n) {
        return NumberFormat.getIntegerInstance().format(n);
    }
}
