package org.herac.tuxguitar.io.tef3.base;

import java.util.HashMap;

public class TEChordDefinition {
    public enum TEChordFretSymbol
    {
        Dot(0),
        Thumb(1),
        FingerOne(2),
        FingerTwo(3),
        FingerThree(4),
        MinusTwoFlatMinusSecond(5),
        NoFinger(7);

        private final int value;

        private TEChordFretSymbol(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEChordFretSymbol> enumValues = new HashMap<>();

        public static TEChordFretSymbol getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }

            TEChordFretSymbol foundEnumValue = null;
            for (TEChordFretSymbol enumValue : values())
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

    private int[] frets;
    private TEChordFretSymbol[] fretSymbols;
    private String name;
    private int anchorFret;

    public TEChordDefinition(int[] frets, TEChordFretSymbol[] fretSymbols, String name, int anchorFret) {
        this.frets = frets;
        this.fretSymbols = fretSymbols;
        this.name = name;
        this.anchorFret = anchorFret;
    }

    public int[] getFrets() {
        return this.frets;
    }

    public TEChordFretSymbol[] getFretSymbols() {
        return this.fretSymbols;
    }

    public String getName() {
        return this.name;
    }

    public int getAnchorFret() {
        return this.anchorFret;
    }
}
