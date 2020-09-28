package apple.questing.data;

import apple.questing.data.combo.FinalQuestCombo;

public class FinalQuestOptionsAll {
    public FinalQuestOptions answerPercAPT;
    public FinalQuestOptions answerPercTime;
    public FinalQuestOptions answerAmountAPT;
    public FinalQuestOptions answerAmountTime;
    public FinalQuestOptions answerTimeAPT;
    public FinalQuestOptions answerTimeAmount;

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
}