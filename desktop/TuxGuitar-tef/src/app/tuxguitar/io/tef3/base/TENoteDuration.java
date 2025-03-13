package org.herac.tuxguitar.io.tef3.base;

import java.util.HashMap;

public enum TENoteDuration
{
    WholeNote(0),
    DottedHalfNote(1),
    WholeNoteTriplet(2),
    HalfNote(3),
    DottedQuarterNote(4),
    HalfNoteTriplet(5),
    QuarterNote(6),
    DottedEigthNote(7),
    QuarterNoteTriplet(8),
    EigthNote(9),
    DottedSixteenthNote(10),
    EigthNoteTriplet(11),
    SixteenthNote(12),
    DottedThirtySecondNote(13),
    SixteenthNoteTriplet(14),
    ThirtySecondNote(15),
    DottedSixtyFourthNote(16),
    ThirtySecondNoteTriplet(17),
    SixtyFourthNote(18),
    DoubleDottedHalf(19),
    // Filler numbers. 20 = Sixteenth. 21 = Sixty Fourth.
    DoubleDottedQuarter(22),
    // Filler numbers. 23 = Sixteenth. 24 = Sixty Fourth.
    DoubleDottedEigthth(25),
    // Filler numbers. 26 = Sixteenth. 27 = Sixty Fourth.
    DoubleDottedSixteenth(28),
    // Filler numbers. 29 = Sixteenth. 30 = Sixty Fourth.
    DottedWholeNote(31);

    private int value;

    private TENoteDuration(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    private static HashMap<Integer, TENoteDuration> enumValues = new HashMap<>();

    public static TENoteDuration getEnumFromInt(int i) {
        if (enumValues.containsKey(i)) {
            return enumValues.get(i);
        }

        TENoteDuration foundEnumValue = null;
        for (TENoteDuration enumValue : values())
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
