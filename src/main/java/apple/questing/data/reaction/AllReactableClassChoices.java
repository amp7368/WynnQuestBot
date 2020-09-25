package apple.questing.data.reaction;


import java.util.LinkedList;
import java.util.List;

public class AllReactableClassChoices {
    private static final long REMEMBER_MESSAGE_ID_TIME = 1000 * 60 * 5; // 5 minutes

    // sorted from oldest to newest
    private static final List<ClassChoiceMessage> messageIds = new LinkedList<>();
    private static final Object syncObject = new Object();

    public static ClassChoiceMessage getMessage(String messageId) {
        synchronized (syncObject) {
            while (!messageIds.isEmpty()) {
                if (System.currentTimeMillis() - messageIds.get(0).time > REMEMBER_MESSAGE_ID_TIME)
                    messageIds.remove(0);
                else
                    break;
            }
        }
        for (ClassChoiceMessage rememberedMessageId : messageIds) {
            if (rememberedMessageId.id.equals(messageId)) {
                // we found our message
                messageIds.remove(rememberedMessageId);
                return rememberedMessageId;
            }
        }
        return null;
    }

    public static void addMessage(ClassChoiceMessage classChoiceMessage) {
        synchronized (syncObject) {
            messageIds.add(classChoiceMessage);
        }
    }
}
