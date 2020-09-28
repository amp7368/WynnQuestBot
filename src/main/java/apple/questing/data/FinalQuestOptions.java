package apple.questing.data;

import apple.questing.data.combo.FinalQuestCombo;

public class FinalQuestOptions {
    public FinalQuestCombo ncnx;
    public FinalQuestCombo cnx;
    public FinalQuestCombo ncx;
    public FinalQuestCombo cx;

    public FinalQuestOptions(FinalQuestCombo ncnx, FinalQuestCombo cnx, FinalQuestCombo ncx, FinalQuestCombo cx) {
        this.ncnx = ncnx;
        this.cnx = cnx;
        this.ncx = ncx;
        this.cx = cx;
    }
}
