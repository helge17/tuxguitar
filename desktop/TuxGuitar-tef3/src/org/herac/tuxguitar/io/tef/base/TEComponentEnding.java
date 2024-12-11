package org.herac.tuxguitar.io.tef3.base;

import java.util.HashMap;

public class TEComponentEnding extends TEComponentBase {
    public enum TEComponentEndingFlag
    {
        NoEndingFlag(0),
        End(1),
        DaSigno(2),
        Signo(3),
        AlCoda(4),
        Coda(5);

        private int value;

        private TEComponentEndingFlag(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentEndingFlag> enumValues = new HashMap<>();

        public static TEComponentEndingFlag getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentEndingFlag foundEnumValue = null;
            for (TEComponentEndingFlag enumValue : values())
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

    private int endingNumber;
    private TEComponentEndingFlag endingFlags;
    private boolean isOpenBracket;
    private boolean isCloseBracket;
    private int yPosition;
    private int xPosition;

    public TEComponentEnding(TEPosition position, int endingNumber, TEComponentEndingFlag endingFlags, boolean isOpenBracket,
        boolean isCloseBracket, int yPosition, int xPosition)
    {
        super(position);

        this.endingNumber = endingNumber;
        this.endingFlags = endingFlags;
        this.isOpenBracket = isOpenBracket;
        this.isCloseBracket = isCloseBracket;
        this.yPosition = yPosition;
        this.xPosition = xPosition;
    }

    public int getEndingNumber() {
        return this.endingNumber;
    }

    public TEComponentEndingFlag getEndingFlags() {
        return this.endingFlags;
    }

    public boolean getIsOpenBracket() {
        return this.isOpenBracket;
    }

    public boolean getIsCloseBracket() {
        return this.isCloseBracket;
    }

    public int getYposition() {
        return this.yPosition;
    }

    public int getXposition() {
        return this.xPosition;
    }

    public int getSortOrder() {
        return 130;
    }
}
