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

    public void sortByAPT() {
        if (ncnx != null)
            ncnx.sortByAPT();
        if (cnx != null)
            cnx.sortByAPT();
        if (ncx != null)
            ncx.sortByAPT();
        if (cx != null)
            cx.sortByAPT();
    }

    public FinalQuestCombo get(FinalQuestOptionsAll.Answer.CX cx) {
        switch (cx) {
            case CX:
                return this.cx;
            case CNX:
                return this.cnx;
            case NCX:
                return this.ncx;
            case NCNX:
                return this.ncnx;
        }
        return null;
    }
}
