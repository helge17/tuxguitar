package org.herac.tuxguitar.io.tef3.base;

import java.util.HashMap;

public class TETrack {
    public enum TETrackTransposition
    {
        NoTransposition(0),
        PlusThreeSharpPlusNinth(1),
        PlusTwoSharpPlusSecond(2),
        PlusOneSharpPlusSeventh(3),
        MinusOneFlatMinusSeventh(5),
        MinusTwoFlatMinusSecond(6),
        MinusThreeFlatMinusNinth(7);

        private int value;

        private TETrackTransposition(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TETrackTransposition> enumValues = new HashMap<>();

        public static TETrackTransposition getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }

            TETrackTransposition foundEnumValue = null;
            for (TETrackTransposition enumValue : values())
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

    public enum TETrackMiddleCoffset
    {
        MiddleC15b(-12),
        MiddleC8vb(0),
        MiddleCStandard(12),
        MiddleC8va(24);

        private int value;

        private TETrackMiddleCoffset(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TETrackMiddleCoffset> enumValues = new HashMap<>();

        public static TETrackMiddleCoffset getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }

            TETrackMiddleCoffset foundEnumValue = null;
            for (TETrackMiddleCoffset enumValue : values())
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

    public enum TETrackClef
    {
        NoClef(0),
        TrebleClef(1),
        BassClef(2),
        BaritoneBassClef(3),
        TenorClef(4),
        AltoClef(5),
        MezzoSuprano(6),
        Suprano(7);

        private int value;

        private TETrackClef(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TETrackClef> enumValues = new HashMap<>();

        public static TETrackClef getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }

            TETrackClef foundEnumValue = null;
            for (TETrackClef enumValue : values())
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

    private int stringCount;
    private int midiInstrument;
    private TETrackTransposition transposition;
    private int capo;
    private TETrackMiddleCoffset middleCoffset;
    private TETrackClef clef;
    private boolean isGrandStaff;
    private boolean isSquareBracket;
    private int pan;
    private int volume;
    private boolean isDoubleStrings;
    private boolean isLetRing;
    private boolean isPedalSteelGuitar;
    private boolean isMultipleAudioChannels;
    private boolean isRhythmTrack;
    private byte[] tuning;
    private String trackName;

    public TETrack(int stringCount,
        int midiInstrument,
        TETrackTransposition transposition,
        int capo,
        TETrackMiddleCoffset middleCoffset,
        TETrackClef clef,
        boolean isGrandStaff,
        boolean isSquareBracket,
        int pan,
        int volume,
        boolean isDoubleStrings,
        boolean isLetRing,
        boolean isPedalSteelGuitar,
        boolean isMultipleAudioChannels,
        boolean isRhythmTrack,
        byte[] tuning,
        String trackName)
    {
        this.stringCount = stringCount;
        this.midiInstrument = midiInstrument;
        this.transposition = transposition;
        this.capo = capo;
        this.middleCoffset = middleCoffset;
        this.clef = clef;
        this.isGrandStaff = isGrandStaff;
        this.isSquareBracket = isSquareBracket;
        this.pan = pan;
        this.volume = volume;
        this.isDoubleStrings = isDoubleStrings;
        this.isLetRing = isLetRing;
        this.isPedalSteelGuitar = isPedalSteelGuitar;
        this.isMultipleAudioChannels = isMultipleAudioChannels;
        this.isRhythmTrack = isRhythmTrack;
        this.tuning = tuning;
        this.trackName = trackName;
    }

    public int getStringCount() {
        return this.stringCount;
    }

    public int getMidiInstrument() {
        return this.midiInstrument;
    }

    public TETrackTransposition getTransposition() {
        return this.transposition;
    }

    public int getCapo() {
        return this.capo;
    }

    public TETrackMiddleCoffset getMiddleCoffset() {
        return this.middleCoffset;
    }

    public TETrackClef getClef() {
        return this.clef;
    }

    public boolean getIsGrandStaff() {
        return this.isGrandStaff;
    }

    public boolean getIsSquareBracket() {
        return this.isSquareBracket;
    }

    public int getPan() {
        return this.pan;
    }

    public int getVolume() {
        return this.volume;
    }

    public boolean getIsDoubleStrings() {
        return this.isDoubleStrings;
    }

    public boolean getIsLetRing() {
        return this.isLetRing;
    }

    public boolean getIsPedalSteelGuitar() {
        return this.isPedalSteelGuitar;
    }

    public boolean getIsMultipleAudioChannels() {
        return this.isMultipleAudioChannels;
    }

    public boolean getIsRhythmTrack() {
        return this.isRhythmTrack;
    }

    public byte[] getTuning() {
        return this.tuning;
    }

    public String getTrackName() {
        return this.trackName;
    }

    public boolean getIsPercussion() {
        return this.midiInstrument == 96;
    }
}
