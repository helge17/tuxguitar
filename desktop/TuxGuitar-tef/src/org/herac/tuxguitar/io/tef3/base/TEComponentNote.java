package org.herac.tuxguitar.io.tef3.base;

import java.util.HashMap;

public class TEComponentNote extends TEComponentBase {
    public enum TEComponentNoteDynamics
    {
        FFF(0),
        FF(1),
        F(2),
        MF(3),
        MP(4),
        P(5),
        PP(6),
        PPP(7);

        private int value;

        private TEComponentNoteDynamics(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentNoteDynamics> enumValues = new HashMap<>();

        public static TEComponentNoteDynamics getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentNoteDynamics foundEnumValue = null;
            for (TEComponentNoteDynamics enumValue : values())
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

    public enum TEComponentNoteEffect1
    {
        NoEffect1(0),
        HammerOn(1),
        PullOff(2),
        Slide(3),
        Choke(4),
        Brush(5),
        NaturalHarmonic(6),
        ArtificialHarmonic(7),
        PalmMute(8),
        Tap(9),
        Vibrato(10),
        Tremolo(11),
        Bend(12),
        BendRelease(13),
        Roll(14),
        DeadNote(15);

        private int value;

        private TEComponentNoteEffect1(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentNoteEffect1> enumValues = new HashMap<>();

        public static TEComponentNoteEffect1 getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentNoteEffect1 foundEnumValue = null;
            for (TEComponentNoteEffect1 enumValue : values())
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

    public enum TEComponentNoteAttributes
    {
        NoAttributes(0),
        UpperVoice(2),
        LowerVoice(3);

        private int value;

        private TEComponentNoteAttributes(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentNoteAttributes> enumValues = new HashMap<>();

        public static TEComponentNoteAttributes getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentNoteAttributes foundEnumValue = null;
            for (TEComponentNoteAttributes enumValue : values())
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

    public enum TEComponentNoteAlterations
    {
        NoAlterations(0),
        ForceSharp(1),
        ForceFlat(2),
        ForceNatural(3);

        private int value;

        private TEComponentNoteAlterations(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentNoteAlterations> enumValues = new HashMap<>();

        public static TEComponentNoteAlterations getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentNoteAlterations foundEnumValue = null;
            for (TEComponentNoteAlterations enumValue : values())
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

    public enum TEComponentNotePitchShift
    {
        NoPitchShift(0),
        MinusThreeSemitones(1),
        MinusTwoSemitones(2),
        MinusOneSemitone(3),
        PlusOneSemitone(5),
        PlusTwoSemitones(6),
        PlusThreeSemitones(7);

        private int value;

        private TEComponentNotePitchShift(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentNotePitchShift> enumValues = new HashMap<>();

        public static TEComponentNotePitchShift getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentNotePitchShift foundEnumValue = null;
            for (TEComponentNotePitchShift enumValue : values())
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

    public enum TEComponentNoteGraceNoteEffect
    {
        NoGraceNoteEffect(0),
        GraceHammerOn(1),
        GraceSlide(2),
        GraceBendRelease(3),
        GraceMordent(4),
        GraceDouble(5),
        GraceGruppetto(6),
        GraceTrill(7);

        private int value;

        private TEComponentNoteGraceNoteEffect(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentNoteGraceNoteEffect> enumValues = new HashMap<>();

        public static TEComponentNoteGraceNoteEffect getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentNoteGraceNoteEffect foundEnumValue = null;
            for (TEComponentNoteGraceNoteEffect enumValue : values())
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

    public enum TEComponentNoteEffect2
    {
        NoEffect2(0),
        LetRing(1),
        Slap(2),
        Rasgueado(3),
        GhostNote(4),
        TremUpOrDown(5),
        TremDiveReturn(6),
        Staccato(7),
        FadeIn(8),
        FadeOut(9),
        HideInNotation(15);

        private int value;

        private TEComponentNoteEffect2(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentNoteEffect2> enumValues = new HashMap<>();

        public static TEComponentNoteEffect2 getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentNoteEffect2 foundEnumValue = null;
            for (TEComponentNoteEffect2 enumValue : values())
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

    public enum TEComponentNoteEffect3
    {
        NoEffect3(0),
        HammerOn(1),
        PullOff(2),
        Roll(3),
        Choke(4),
        Brush(5),
        NaturalHarmonic(6),
        ArtificialHarmonic(7),
        LetRing(8),
        GhostNote(9),
        DeadNote(10),
        Variation(11);

        private int value;

        private TEComponentNoteEffect3(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentNoteEffect3> enumValues = new HashMap<>();

        public static TEComponentNoteEffect3 getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentNoteEffect3 foundEnumValue = null;
            for (TEComponentNoteEffect3 enumValue : values())
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

    public enum TEComponentNoteFingering
    {
        NoFingering(0),
        FingerThumb(1),
        FingerOne(2),
        FingerTwo(3),
        FingerThree(4),
        Finger4(5);

        private int value;

        private TEComponentNoteFingering(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentNoteFingering> enumValues = new HashMap<>();

        public static TEComponentNoteFingering getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentNoteFingering foundEnumValue = null;
            for (TEComponentNoteFingering enumValue : values())
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

    public enum TEComponentNoteStroke
    {
        NoStroke(0),
        ThumbStroke(1),
        OtherFingerStroke(2),
        UpStroke(3),
        DownStroke(4),
        FingerAboveTab(5),
        IntoNotation(7);

        private int value;

        private TEComponentNoteStroke(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        private static HashMap<Integer, TEComponentNoteStroke> enumValues = new HashMap<>();

        public static TEComponentNoteStroke getEnumFromInt(int i) {
            if (enumValues.containsKey(i)) {
                return enumValues.get(i);
            }
    
            TEComponentNoteStroke foundEnumValue = null;
            for (TEComponentNoteStroke enumValue : values())
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

    private int fret;
    private boolean isGraceNote;
    private boolean isPitchShifted;
    private TENoteDuration duration;
    private TEComponentNoteDynamics dynamics;
    private TEComponentNoteEffect1 effect1;
    private TEComponentNoteAttributes attributes;
    private TEComponentNoteAlterations alterations;
    private TEComponentNotePitchShift pitchShift;
    private TEComponentNoteGraceNoteEffect graceNoteEffect;
    private int graceNoteFret;
    private TEComponentNoteEffect2 effect2;
    private TEComponentNoteEffect3 effect3;
    private TEFontPreset fontPreset;
    private boolean isStabilo;
    private TEComponentNoteFingering firstFinger;
    private TEComponentNoteFingering secondFinger;
    private TEComponentNoteStroke stroke;
    private boolean hasNoStemNoFlag;
    private boolean isExcludedFromBeaming;
    private boolean isMoveToLeft;
    private boolean isMoveToRight;
    private boolean isTieUpward;
    private boolean isTieDownward;
    private boolean isOttavaBassa;
    private boolean isOttavaAlta;

    public TEComponentNote(TEPosition position,
        int fret,
        boolean isGraceNote,
        boolean isPitchShifted,
        TENoteDuration duration,
        TEComponentNoteDynamics dynamics,
        TEComponentNoteEffect1 effect1,
        TEComponentNoteAttributes attributes,
        TEComponentNoteAlterations alterations,
        TEComponentNotePitchShift pitchShift,
        TEComponentNoteGraceNoteEffect graceNoteEffect,
        int graceNoteFret,
        TEComponentNoteEffect2 effect2,
        TEComponentNoteEffect3 effect3,
        TEFontPreset fontPreset,
        boolean isStabilo,
        TEComponentNoteFingering firstFinger,
        TEComponentNoteFingering secondFinger,
        TEComponentNoteStroke stroke,
        boolean hasNoStemNoFlag,
        boolean isExcludedFromBeaming,
        boolean isMoveToLeft,
        boolean isMoveToRight,
        boolean isTieUpward,
        boolean isTieDownward,
        boolean isOttavaBassa,
        boolean isOttavaAlta)
    {
        super(position);

        this.fret = fret;
        this.isGraceNote = isGraceNote;
        this.isPitchShifted = isPitchShifted;
        this.duration = duration;
        this.dynamics = dynamics;
        this.effect1 = effect1;
        this.attributes = attributes;
        this.alterations = alterations;
        this.pitchShift = pitchShift;
        this.graceNoteEffect = graceNoteEffect;
        this.graceNoteFret = graceNoteFret;
        this.effect2 = effect2;
        this.effect3 = effect3;
        this.fontPreset = fontPreset;
        this.isStabilo = isStabilo;
        this.firstFinger = firstFinger;
        this.secondFinger = secondFinger;
        this.stroke = stroke;
        this.hasNoStemNoFlag = hasNoStemNoFlag;
        this.isExcludedFromBeaming = isExcludedFromBeaming;
        this.isMoveToLeft = isMoveToLeft;
        this.isMoveToRight = isMoveToRight;
        this.isTieUpward = isTieUpward;
        this.isTieDownward = isTieDownward;
        this.isOttavaBassa = isOttavaBassa;
        this.isOttavaAlta = isOttavaAlta;
    }

    public int getFret() {
        return this.fret;
    }

    public boolean getIsGraceNote() {
        return this.isGraceNote;
    }

    public boolean getIsPitchShifted() {
        return this.isPitchShifted;
    }

    public TENoteDuration getDuration() {
        return this.duration;
    }

    public TEComponentNoteDynamics getDynamics() {
        return this.dynamics;
    }

    public TEComponentNoteEffect1 getEffect1() {
        return this.effect1;
    }

    public TEComponentNoteAttributes getAttributes() {
        return this.attributes;
    }

    public TEComponentNoteAlterations getAlterations() {
        return this.alterations;
    }

    public TEComponentNotePitchShift getPitchShift() {
        return this.pitchShift;
    }

    public TEComponentNoteGraceNoteEffect getGraceNoteEffect() {
        return this.graceNoteEffect;
    }

    public int getGraceNoteFret() {
        return this.graceNoteFret;
    }

    public TEComponentNoteEffect2 getEffect2() {
        return this.effect2;
    }

    public TEComponentNoteEffect3 getEffect3() {
        return this.effect3;
    }

    public TEFontPreset getFontPreset() {
        return this.fontPreset;
    }

    public boolean getIsStabilo() {
        return this.isStabilo;
    }
    
    public TEComponentNoteFingering getFirstFinger() {
        return this.firstFinger;
    }

    public TEComponentNoteFingering getSecondFinger() {
        return this.secondFinger;
    }
    
    public TEComponentNoteStroke getStroke() {
        return this.stroke;
    }

    public boolean getHasNoStemNoFlag() {
        return this.hasNoStemNoFlag;
    }

    public boolean getIsExcludedFromBeaming() {
        return this.isExcludedFromBeaming;
    }

    public boolean getIsMoveToLeft() {
        return this.isMoveToLeft;
    }

    public boolean getIsMoveToRight() {
        return this.isMoveToRight;
    }

    public boolean getIsTieUpward() {
        return this.isTieUpward;
    }

    public boolean getIsTieDownward() {
        return this.isTieDownward;
    }

    public boolean getIsOttavaBassa() {
        return this.isOttavaBassa;
    }

    public boolean getIsOttavaAlta() {
        return this.isOttavaAlta;
    }

    public int getSortOrder() {
        return 0;
    }
}
