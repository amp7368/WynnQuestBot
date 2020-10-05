package apple.questing.discord.commands;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class DetermineArguments {
    /**
     * determines the amount desired argument from the arguments given
     *
     * @param contentSplit the message split by spaces. this is modified as find arguments
     * @param channel      the channel to send an error message to if there is one
     * @return -2 if there was an error | -1 if the argument was not found | amountDesired, if all was good
     */
    public static long determineAmountDesired(List<String> contentSplit, TextChannel channel) {
        int size = contentSplit.size();
        for (int i = 0; i < size; i++) {
            if (contentSplit.get(i).equals("-e")) {
                if (size == i + 1) {
                    // user did -e at the end of their message without an argument
                    channel.sendMessage("-e requires a number after it to specify how many emeralds or xp you want from quests").queue();
                    return -2;
                } else {
                    // this is fine because if we reach this we exit the loop and return
                    // noinspection SuspiciousListRemoveInLoop
                    contentSplit.remove(i);
                    String amount = contentSplit.remove(i);
                    try {
                        long amountFound = Long.parseLong(amount);
                        if (amountFound <= 0) {
                            channel.sendMessage(String.format("'%s' is a hard amount to rationalize..", amount)).queue();
                            return -2;
                        }
                        return amountFound;
                    } catch (NumberFormatException e) {
                        // user's -e argument is not a number
                        channel.sendMessage("-e requires a number after it to specify how many emeralds or xp you want from quests\n'" + amount + "' is not a number.").queue();
                        return -2;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * determines the classLevel argument from the arguments given
     *
     * @param contentSplit the message split by spaces. this is modified as find arguments
     * @param channel      the channel to send an error message to if there is one
     * @return -2 if there was an error | -1 if the argument was not found | classLevel, if all was good
     */
    public static int determineClassLevel(List<String> contentSplit, TextChannel channel) {
        int size = contentSplit.size();
        for (int i = 0; i < size; i++) {
            if (contentSplit.get(i).equals("-l")) {
                if (size == i + 1) {
                    // user did -t at the end of their message without an argument
                    channel.sendMessage("-l requires a number after it to specify how much time you want to spend doing quests").queue();
                    return -2;
                } else {
                    // this is fine because if we reach this we exit the loop and return
                    // noinspection SuspiciousListRemoveInLoop
                    contentSplit.remove(i);
                    String time = contentSplit.remove(i);
                    try {
                        final int timeFound = Integer.parseInt(time);
                        if (timeFound <= 0) {
                            channel.sendMessage(String.format("'%s' is a hard class level to rationalize..", time)).queue();
                            return -2;
                        }
                        return timeFound;
                    } catch (NumberFormatException e) {
                        // user's -t argument is not a number
                        channel.sendMessage("-l requires a number after it to specify how much time you want to spend doing quests\n'" + time + "' is not a number.").queue();
                        return -2;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * determines the timeToSpend argument from the arguments given
     *
     * @param contentSplit the message split by spaces. this is modified as find arguments
     * @param channel      the channel to send an error message to if there is one
     * @return -2 if there was an error | -1 if the argument was not found | timeToSpend, if all was good
     */
    public static long determineTimeToSpend(List<String> contentSplit, TextChannel channel) {
        int size = contentSplit.size();
        for (int i = 0; i < size; i++) {
            if (contentSplit.get(i).equals("-t")) {
                if (size == i + 1) {
                    // user did -t at the end of their message without an argument
                    channel.sendMessage("-t requires a number after it to specify how much time you want to spend doing quests").queue();
                    return -2;
                } else {
                    // this is fine because if we reach this we exit the loop and return
                    // noinspection SuspiciousListRemoveInLoop
                    contentSplit.remove(i);
                    String time = contentSplit.remove(i);
                    try {
                        final long timeFound = Long.parseLong(time);
                        if (timeFound <= 0) {
                            channel.sendMessage(String.format("'%s' is a hard time to rationalize..", time)).queue();
                            return -2;
                        }
                        return timeFound;
                    } catch (NumberFormatException e) {
                        // user's -t argument is not a number
                        channel.sendMessage("-t requires a number after it to specify how much time you want to spend doing quests\n'" + time + "' is not a number.").queue();
                        return -2;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * determines the percentageDesired argument from the arguments given
     *
     * @param contentSplit the message split by spaces. this is modified as find arguments
     * @param channel      the channel to send an error message to if there is one
     * @return -2 if there was an error | -1 if the argument was not found | percentageDesired, if all was good
     */
    public static double determinePercentageDesired(List<String> contentSplit, TextChannel channel) {
        int size = contentSplit.size();
        for (int i = 0; i < size; i++) {
            if (contentSplit.get(i).equals("-p")) {
                if (size == i + 1) {
                    // user did -t at the end of their message without an argument
                    channel.sendMessage("-p requires a percentage after it to specify what percentage of possible rewards you want").queue();
                    return -2;
                } else {
                    // this is fine because if we reach this we exit the loop and return
                    // noinspection SuspiciousListRemoveInLoop
                    contentSplit.remove(i);
                    String percentageDesired = contentSplit.remove(i);
                    try {
                        final double percentageFound = Double.parseDouble(percentageDesired);
                        if (percentageFound <= 0 || percentageFound > 100) {
                            channel.sendMessage(String.format("'%s' is a hard percentage to rationalize..", percentageFound)).queue();
                            return -2;
                        }
                        return percentageFound / 100;
                    } catch (NumberFormatException e) {
                        // user's -t argument is not a number
                        channel.sendMessage("-p requires a percentage after it to specify what percentage of possible rewards you want\n'"
                                + percentageDesired + "' is not in the format 45.3").queue();
                        return -2;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * determines the isCollection argument from the arguments given
     *
     * @param contentSplit the message split by spaces. this is modified as find arguments
     * @return isCollection argument (false if it exists | true otherwise)
     */
    public static boolean determineIsCollection(List<String> contentSplit) { //find -c if it exists
        return !contentSplit.remove("-c");
    }

    /**
     * determines the isXpDesired argument from the arguments given
     *
     * @param contentSplit the message split by spaces. this is modified as find arguments
     * @return isXpDesired argument (true if it exists | false otherwise)
     */
    public static boolean determineIsXpDesired(List<String> contentSplit) {
        //find -x if it exists
        return contentSplit.remove("-x");
    }
}
