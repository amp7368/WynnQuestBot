package apple.questing.sheets;

public class SheetsRanges {
    public static final String QUESTS_SHEET = "Quests";
    public static final String ALL_QUESTS = QUESTS_SHEET + "!A5:H";

    public enum SheetName {
        OVERVIEW_SHEET_ID(0, "Overview"),
        PERC_APT(1, "% | Amount/Time"),
        PERC_TIME(2, "% | Time"),
        AMOUNT_APT(3, "Amount | Amount/Time"),
        AMOUNT_TIME(4, "Amount | Time"),
        TIME_APT(5, "Time | Amount/Time"),
        TIME_AMOUNT(6, "Time | Amount");
        private final int id;
        private final String name;

        SheetName(int sheetId, String name) {
            this.id = sheetId;
            this.name = name;
        }

        public int getSheetId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
