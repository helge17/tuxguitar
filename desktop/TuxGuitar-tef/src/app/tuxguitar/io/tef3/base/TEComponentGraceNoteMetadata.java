package app.tuxguitar.io.tef3.base;

import java.util.HashMap;

public class TEComponentGraceNoteMetadata extends TEComponentBase {
    public enum TEComponentGraceNoteMetadataDuration
    {
        NoGraceDuration(1),
        GraceThirtySecond(2),
        GraceSixteenth(3),
        GraceEighth(4),
        GraceQuarter(5),
        GraceHalf(6);

        private int value;

        private TEComponentGraceNoteMetadataDuration(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentGraceNoteMetadataDuration> enumValues = new HashMap<>();

        public static TEComponentGraceNoteMetadataDuration getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }

            TEComponentGraceNoteMetadataDuration foundEnumValue = null;
            for (TEComponentGraceNoteMetadataDuration enumValue : values())
            {
                if (i != enumValue.value)
                {
                    continue;
                }

                foundEnumValue = enumValue;
                enumValues.put(i, foundEnumValue);
            }

            return foundEnumValue;
        }
    }

    private TEComponentGraceNoteMetadataDuration duration;
    private int string;
    private boolean isDifferentString;
    private int doubleNoteOne;
    private boolean hasDoubleNoteOne;
    private int doubleNoteTwo;
    private boolean hasDoubleNoteTwo;
    private int xPosition;
    private boolean hasNoFlags;
    private boolean hasNoSlur;
    private boolean hasStemDown;
    private boolean isSharpOrFlat;
    private boolean acciaccatura;

    public TEComponentGraceNoteMetadata(TEPosition position, TEComponentGraceNoteMetadataDuration duration, int string, boolean isDifferentString, int doubleNoteOne,
        boolean hasDoubleNoteOne, int doubleNoteTwo, boolean hasDoubleNoteTwo, int xPosition, boolean hasNoFlags, boolean hasNoSlur, boolean hasStemDown,
        boolean isSharpOrFlat, boolean acciaccatura)
    {
        super(position);

        this.duration = duration;
        this.string = string;
        this.isDifferentString = isDifferentString;
        this.doubleNoteOne = doubleNoteOne;
        this.hasDoubleNoteOne = hasDoubleNoteOne;
        this.doubleNoteTwo = doubleNoteTwo;
        this.hasDoubleNoteTwo = hasDoubleNoteTwo;
        this.xPosition = xPosition;
        this.hasNoFlags = hasNoFlags;
        this.hasNoSlur = hasNoSlur;
        this.hasStemDown = hasStemDown;
        this.isSharpOrFlat = isSharpOrFlat;
        this.acciaccatura = acciaccatura;
    }

    public TEComponentGraceNoteMetadataDuration getDuration() {
        return this.duration;
    }

    public int getString() {
        return this.string;
    }

    public boolean getIsDifferentString() {
        return this.isDifferentString;
    }

    public int getDoubleNoteOne() {
        return this.doubleNoteOne;
    }

    public boolean getHasDoubleNoteOne() {
        return this.hasDoubleNoteOne;
    }

    public int getDoubleNoteTwo() {
        return this.doubleNoteTwo;
    }

    public boolean getHasDoubleNoteTwo() {
        return this.hasDoubleNoteTwo;
    }

    public int getXposition() {
        return this.xPosition;
    }

    public boolean getHasNoFlags() {
        return this.hasNoFlags;
    }

    public boolean getHasNoSlur() {
        return this.hasNoSlur;
    }

    public boolean getHasStemDown() {
        return this.hasStemDown;
    }

    public boolean getIsSharpOrFlat() {
        return this.isSharpOrFlat;
    }

    public boolean getAcciaccatura() {
        return this.acciaccatura;
    }

    public int getSortOrder() {
        return 30;
    }
}
