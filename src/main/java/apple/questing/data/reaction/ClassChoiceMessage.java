package apple.questing.data.reaction;

import apple.questing.data.WynncraftClass;
import apple.questing.data.WynncraftPlayer;

import java.util.Arrays;
import java.util.List;

public class ClassChoiceMessage {
    public final boolean isXpDesired;
    public final boolean isCollection;
    public final long timeToSpend;
    public final long amountDesired;
    public final int classLevel;
    public final long time;
    public final String id;
    public final List<String> classNames;
    public WynncraftPlayer player;
    public final static List<String> emojiAlphabet = Arrays.asList("\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB", "\uD83C\uDDEC", "\uD83C\uDDED",
            "\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1", "\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5", "\uD83C\uDDF6", "\uD83C\uDDF7", "\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA"
            , "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD", "\uD83C\uDDFE", "\uD83C\uDDFF");

    public ClassChoiceMessage(String id, long time, boolean isXpDesired, boolean isCollection, long timeToSpend, long amountDesired, int classLevel, List<String> classNames, WynncraftPlayer player) {
        this.id = id;
        this.time = time;
        this.isXpDesired = isXpDesired;
        this.isCollection = isCollection;
        this.timeToSpend = timeToSpend;
        this.amountDesired = amountDesired;
        this.classLevel = classLevel;
        this.classNames = classNames;
        this.player = player;
    }

    public WynncraftClass getClassFromName(String className) {
        for (WynncraftClass wynncraftClass : player.classes) {
            if (wynncraftClass.name.equals(className)) {
                return wynncraftClass;
            }
        }
        return null;
    }
}