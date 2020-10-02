package apple.questing.data.answer;

import java.util.Arrays;
import java.util.List;

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

    public List<FinalQuestCombo> getList() {
        return Arrays.asList(
                ncnx,
                cnx,
                ncx,
                cx
        );
    }
}
