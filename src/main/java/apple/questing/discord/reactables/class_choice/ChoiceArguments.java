package apple.questing.discord.reactables.class_choice;

import apple.questing.data.player.WynncraftPlayer;

import java.util.List;

public class ChoiceArguments {

    public boolean isXpDesired;
    public boolean isCollection;
    public final long timeToSpend;
    public final long amountDesired;
    public final int classLevel;
    public final List<String> classNames;
    public final WynncraftPlayer player;
    public final boolean isAllClasses;

    public ChoiceArguments(boolean isXpDesired, boolean isCollection, long timeToSpend, long amountDesired, int classLevel, List<String> classNames, WynncraftPlayer player, boolean isAllClasses) {
        this.isXpDesired = isXpDesired;
        this.isCollection = isCollection;
        this.timeToSpend = timeToSpend;
        this.amountDesired = amountDesired;
        this.classLevel = classLevel;
        this.player = player;
        this.classNames = classNames;
        this.isAllClasses = isAllClasses;
    }
}
