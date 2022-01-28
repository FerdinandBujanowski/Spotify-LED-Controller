package control.type_enums;

public enum TimeSignature {


    ONE_SIXTEEN("1 / 16", 1.0 / 16.0),
    ONE_EIGHT("1 / 8", 1.0 / 8.0),
    ONE_FOUR("1 / 4", 1.0 / 4.0),
    TWO_FOUR("2 / 4", 2.0 / 4.0),
    ONE_BAR("1B", 1.0),
    TWO_BAR("2B", 2.0),

    ONE_FOUR_TRIPLE("(1 / 4)T", 1.0 / 12.0),
    TWO_FOUR_TRIPLE("(2 / 4)T", 2.0 / 12.0),
    ONE_BAR_TRIPLE("(1B)T", 1.0 / 3.0);

    private String displayText;
    private double ratio;

    TimeSignature(String displayText, double ratio) {
        this.displayText = displayText;
        this.ratio = ratio;
    }

    private String getDisplayText() {
        return this.displayText;
    }

    public double getRatio() {
        return this.ratio;
    }

    public static int indexOf(TimeSignature timeSignature) {
        TimeSignature[] timeSignatures = TimeSignature.values();
        for(int i = 0; i < timeSignatures.length; i++) {
            if(timeSignature.equals(timeSignatures[i])) {
                return i;
            }
        }
        return -1;
    }

    public static String[] getNameArray() {
        TimeSignature[] timeSignatures = TimeSignature.values();
        String[] displayNames = new String[timeSignatures.length];

        for(int i = 0; i < timeSignatures.length; i++) {
            displayNames[i] = timeSignatures[i].getDisplayText();
        }
        return displayNames;
    }
}
