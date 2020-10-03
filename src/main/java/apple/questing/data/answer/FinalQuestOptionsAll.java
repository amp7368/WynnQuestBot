package apple.questing.data.answer;

import java.util.Arrays;
import java.util.List;

public class FinalQuestOptionsAll {
    public final FinalQuestOptions answerPercAPT;
    public final FinalQuestOptions answerPercTime;
    public final FinalQuestOptions answerAmountAPT;
    public final FinalQuestOptions answerAmountTime;
    public final FinalQuestOptions answerTimeAPT;
    public final FinalQuestOptions answerTimeAmount;

    public FinalQuestOptionsAll(FinalQuestOptions answerPercAPT, FinalQuestOptions answerPercTime,
                                FinalQuestOptions answerAmountAPT, FinalQuestOptions answerAmountTime,
                                FinalQuestOptions answerTimeAPT, FinalQuestOptions answerTimeAmount) {
        this.answerPercAPT = answerPercAPT;
        this.answerPercTime = answerPercTime;
        this.answerAmountAPT = answerAmountAPT;
        this.answerAmountTime = answerAmountTime;
        this.answerTimeAPT = answerTimeAPT;
        this.answerTimeAmount = answerTimeAmount;
    }

    public List<FinalQuestOptions> getList() {
        return Arrays.asList(
                answerPercAPT,
                answerPercTime,
                answerAmountAPT,
                answerAmountTime,
                answerTimeAPT,
                answerTimeAmount
        );
    }

    public FinalQuestCombo get(Answer.Desired desired, Answer.Goal goal, Answer.CX cx) {
        switch (desired) {
            case PERC:
                if (goal == Answer.Goal.APT) {
                    return answerPercAPT == null ? null : answerPercAPT.get(cx);
                } else {
                    return answerPercTime == null ? null : answerPercTime.get(cx);
                }
            case AMOUNT:
                if (goal == Answer.Goal.APT) {
                    return answerAmountAPT == null ? null : answerAmountAPT.get(cx);
                } else {
                    return answerAmountTime == null ? null : answerAmountTime.get(cx);
                }
            case TIME:
                if (goal == Answer.Goal.APT) {
                    return answerTimeAPT == null ? null : answerTimeAPT.get(cx);
                } else {
                    return answerTimeAmount == null ? null : answerTimeAmount.get(cx);
                }
        }
        return null;
    }

    public static class Answer {
        public enum Desired {
            PERC,
            AMOUNT,
            TIME
        }

        public enum Goal {
            APT,
            TIME,
            AMOUNT
        }

        public enum CX {
            CX,
            CNX,
            NCX,
            NCNX
        }
    }
}
