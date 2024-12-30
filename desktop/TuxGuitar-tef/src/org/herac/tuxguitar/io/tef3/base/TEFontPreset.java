package org.herac.tuxguitar.io.tef3.base;

import java.util.HashMap;

public enum TEFontPreset
{
    // Comments relate to the defaults. These are user-swappable.

    Tablature(0),       // Arial 10, Bold
    Title(1),           // Arial 14, Bold
    SmallFont(2),       // Arial 8
    Texts1(3),          // Arial 9
    Texts2(4),          // Times New Roman 9
    Subtitle(5),        // Times New Roman 12, Bold
    Graphics(6),        // Times New Roman 14, Bold
    ChordDiagram(7),    // Arial 7, Bold
    TinyFont(8),        // Helvetica 7
    Custom1(9),         // Arial 10
    Custom2(10),        // Arial 10
    Custom3(11);        // Arial 10

    private int value;

    private TEFontPreset(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    private static HashMap<Integer, TEFontPreset> enumValues = new HashMap<>();

    public static TEFontPreset getEnumFromInt(int i) {
        if (enumValues.containsKey(i)) {
            return enumValues.get(i);
        }

        TEFontPreset foundEnumValue = null;
        for (TEFontPreset enumValue : values())
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
