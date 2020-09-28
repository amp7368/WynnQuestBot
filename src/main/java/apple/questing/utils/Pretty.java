package apple.questing.utils;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

public class Pretty {
    public static String commas(long n) {
        return NumberFormat.getIntegerInstance().format(n);
    }
    @NotNull
    public static String getMon(double amount) {
        int le = (int) (amount / 4096);
        int eb = (int) ((amount / 64) % 64);
        int e = (int) (amount % 64);
        StringBuilder mon = new StringBuilder();
        if (le != 0) {
            mon.append(le);
            mon.append(" le");
            if (eb != 0)
                mon.append(' ');
        }
        if (eb != 0) {
            mon.append(eb);
            mon.append(" eb");
        }
        if (le == 0 && eb == 0 && e == 0) {
            mon.append("0 e");
        } else if (le == 0 || eb == 0) {
            mon.append(" ");
            mon.append(e);
            mon.append(" e");
        }
        return mon.toString();
    }
}
