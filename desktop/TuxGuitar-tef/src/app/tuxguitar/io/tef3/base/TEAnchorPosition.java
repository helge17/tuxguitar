package app.tuxguitar.io.tef3.base;

import java.util.HashMap;

public enum TEAnchorPosition {
    AnchorAboveTablature(0),
    AnchorBelowTablature(1),
    AnchorAboveNotation(2),
    AnchorIntoTablature(3);

    private int value;

    private TEAnchorPosition(int value) {
        this.value = value;
    }

    private static HashMap<Integer, TEAnchorPosition> enumValues = new HashMap<>();

    public static TEAnchorPosition getEnumFromInt(int i) {
        if (enumValues.containsKey(i)) {
            return enumValues.get(i);
        }

        TEAnchorPosition foundEnumValue = null;
        for (TEAnchorPosition enumValue : values())
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