package apple.questing.data;

public class FinalQuestOptions {
    public FinalQuestCombo bestAmountPerTime;
    public FinalQuestCombo bestUtilization;

    public FinalQuestOptions(FinalQuestCombo bestAmountPerTime, FinalQuestCombo bestUtilization) {
        this.bestAmountPerTime = bestAmountPerTime;
        this.bestUtilization = bestUtilization;
    }

    public void print() {
        System.out.printf("bestAmountPerTime: A/T: %f ||| A: %d T: %f\n", bestAmountPerTime.amountPerTime(),bestAmountPerTime.getAmount(),bestAmountPerTime.getTime());
        for (Quest quest : bestAmountPerTime.getQuests()) {
            System.out.print(quest.name + ", ");
        }
        System.out.println();

        System.out.printf("bestUtilization: A/T: %f ||| A: %d T: %f\n", bestUtilization.amountPerTime(),bestUtilization.getAmount(),bestUtilization.getTime());
        for (Quest quest : bestUtilization.getQuests()) {
            System.out.print(quest.name + ", ");
        }
        System.out.println();
    }
}
