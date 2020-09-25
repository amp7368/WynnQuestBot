package apple.questing.data;

public class FinalQuestOptions {
    public FinalQuestCombo bestAmountPerTime;
    public FinalQuestCombo bestUtilization;

    public FinalQuestOptions(FinalQuestCombo bestAmountPerTime, FinalQuestCombo bestUtilization) {
        this.bestAmountPerTime = bestAmountPerTime;
        this.bestUtilization = bestUtilization;
    }

    public void print() {
        System.out.println(bestAmountPerTime.amountPerTime());
        for (Quest quest : bestAmountPerTime.getQuests()) {
            System.out.print(quest.name + ", ");
        }
        System.out.println();
    }
}
