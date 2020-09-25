package apple.questing.data.reaction;

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

    public ClassChoiceMessage(String id, long time, boolean isXpDesired, boolean isCollection, long timeToSpend, long amountDesired, int classLevel, List<String> classNames) {
        this.id = id;
        this.time = time;
        this.isXpDesired = isXpDesired;
        this.isCollection = isCollection;
        this.timeToSpend = timeToSpend;
        this.amountDesired = amountDesired;
        this.classLevel = classLevel;
        this.classNames = classNames;
    }
}
