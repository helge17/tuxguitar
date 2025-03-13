package app.tuxguitar.io.tef3.base;

public class TEComponentRest extends TEComponentBase {
    private TENoteDuration duration;
    private boolean isUpperVoice;
    private boolean isLowerVoice;
    private int yPosition;
    private boolean isSecondaryBeamBreak;
    private boolean isHiddenInNotation;
    private boolean hasNoStemNoFlag;
    private boolean isExcludedFromBeaming;
    private boolean isMoveToLeft;
    private boolean isMoveToRight;
    private boolean isHiddenInTablature;

    public TEComponentRest(TEPosition position, TENoteDuration duration, boolean isUpperVoice, boolean isLowerVoice, int yPosition,
        boolean isSecondaryBeamBreak, boolean isHiddenInNotation, boolean hasNoStemNoFlag, boolean isExcludedFromBeaming, boolean isMoveToLeft,
        boolean isMoveToRight, boolean isHiddenInTablature) {
        super(position);

        this.duration = duration;
        this.isUpperVoice = isUpperVoice;
        this.isLowerVoice = isLowerVoice;
        this.yPosition = yPosition;
        this.isSecondaryBeamBreak = isSecondaryBeamBreak;
        this.isHiddenInNotation = isHiddenInNotation;
        this.hasNoStemNoFlag = hasNoStemNoFlag;
        this.isExcludedFromBeaming = isExcludedFromBeaming;
        this.isMoveToLeft = isMoveToLeft;
        this.isMoveToRight = isMoveToRight;
        this.isHiddenInTablature = isHiddenInTablature;
    }

    public TENoteDuration getDuration() {
        return this.duration;
    }

    public boolean getIsUpperVoice() {
        return this.isUpperVoice;
    }

    public boolean getIsLowerVoice() {
        return this.isLowerVoice;
    }

    public int getYposition() {
        return this.yPosition;
    }

    public boolean getIsSecondaryBeamBreak() {
        return this.isSecondaryBeamBreak;
    }

    public boolean getIsHiddenInNotation() {
        return this.isHiddenInNotation;
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

    public boolean getIsHiddenInTablature() {
        return this.isHiddenInTablature;
    }

    public int getSortOrder() {
        return 20;
    }
}
