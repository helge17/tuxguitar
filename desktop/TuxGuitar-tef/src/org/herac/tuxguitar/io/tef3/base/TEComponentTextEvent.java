package org.herac.tuxguitar.io.tef3.base;

import java.util.HashMap;

public class TEComponentTextEvent extends TEComponentBase {
    public enum TEComponentTextEventBorderType
    {
        NoBorder(0),
        Rectangle(1),
        Round(2);

        private int value;

        private TEComponentTextEventBorderType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentTextEventBorderType> enumValues = new HashMap<>();

        public static TEComponentTextEventBorderType getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentTextEventBorderType foundEnumValue = null;
            for (TEComponentTextEventBorderType enumValue : values())
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

    private int textIndex;
    private int yPosition;
    private int xPosition;
    private TEFontPreset fontPreset;
    private boolean isCentered;
    private TEComponentTextEventBorderType borderType;
    private TEAnchorPosition anchorPosition;

    public TEComponentTextEvent(TEPosition position, int textIndex, int yPosition, int xPosition, TEFontPreset fontPreset,
        boolean isCentered, TEComponentTextEventBorderType borderType, TEAnchorPosition anchorPosition)
    {
        super(position);

        this.textIndex = textIndex;
        this.yPosition = yPosition;
        this.xPosition = xPosition;
        this.fontPreset = fontPreset;
        this.isCentered = isCentered;
        this.borderType = borderType;
        this.anchorPosition = anchorPosition;
    }

    public int getTextIndex() {
        return this.textIndex;
    }

    public int getYposition() {
        return this.yPosition;
    }

    public int getXposition() {
        return this.xPosition;
    }

    public TEFontPreset getFontPreset() {
        return this.fontPreset;
    }

    public boolean getIsCentered() {
        return this.isCentered;
    }

    public TEComponentTextEventBorderType getBorderType() {
        return this.borderType;
    }

    public TEAnchorPosition getAnchorPosition() {
        return this.anchorPosition;
    }

    public int getSortOrder() {
        return 60;
    }
}
