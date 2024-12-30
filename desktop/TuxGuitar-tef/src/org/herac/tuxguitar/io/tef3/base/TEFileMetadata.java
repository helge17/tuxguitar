package org.herac.tuxguitar.io.tef3.base;

import java.util.HashMap;

public class TEFileMetadata {
    public enum TEFileMetadataSyncopation
    {
        ThirtyThreeSixtySix(-2),
        FourtySixty(-1),
        StraightEigths(0),
        JazzEighths(1),
        SwingEighths(2);

        private int value;

        private TEFileMetadataSyncopation(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEFileMetadataSyncopation> enumValues = new HashMap<>();

        public static TEFileMetadataSyncopation getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEFileMetadataSyncopation foundEnumValue = null;
            for (TEFileMetadataSyncopation enumValue : values())
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

    private int majorVersion;

    private int initialBpm;

    private int toneChorus;
    private int toneReverb;
    
    private TEFileMetadataSyncopation syncopation;

    private boolean hasTextEvents;
    private boolean hasChords;
    private boolean hasCopyright;
    private boolean hasReadingList;
    private boolean hasUrl;

    public TEFileMetadata(int majorVersion, int initialBpm, int toneChorus, int toneReverb, TEFileMetadataSyncopation syncopation,
    boolean hasTextEvents, boolean hasChords, boolean hasCopyright, boolean hasReadingList, boolean hasUrl) {
        this.majorVersion = majorVersion;
        this.initialBpm = initialBpm;
        this.toneChorus = toneChorus;
        this.toneReverb = toneReverb;
        this.syncopation = syncopation;
        this.hasTextEvents = hasTextEvents;
        this.hasChords = hasChords;
        this.hasCopyright = hasCopyright;
        this.hasReadingList = hasReadingList;
        this.hasUrl = hasUrl;
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public int getInitialBpm() {
        return this.initialBpm;
    }

    public int getToneChorus() {
        return this.toneChorus;
    }

    public int getToneReverb() {
        return this.toneReverb;
    }

    public TEFileMetadataSyncopation getSyncopation() {
        return this.syncopation;
    }

    public boolean getHasTextEvents() {
        return this.hasTextEvents;
    }

    public boolean getHasChords() {
        return this.hasChords;
    }

    public boolean getHasCopyright() {
        return this.hasCopyright;
    }

    public boolean getHasReadingList() {
        return this.hasReadingList;
    }

    public boolean getHasUrl() {
        return this.hasUrl;
    }
}
