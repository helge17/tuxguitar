package org.herac.tuxguitar.io.tef2.base;

public class TEComponentEnding extends TEComponent {
    private boolean isOpenBracket;
    private boolean isCloseBracket;
    private int endingNumber;

    public TEComponentEnding(int position,int measure, int string, boolean isOpenBracket, boolean isCloseBracket, int endingNumber) {
        super(position, measure, string);

        this.isOpenBracket = isOpenBracket;
        this.isCloseBracket = isCloseBracket;
        this.endingNumber = endingNumber;
    }

    public boolean getIsOpenBracket() {
        return this.isOpenBracket;
    }

    public boolean getIsCloseBracket() {
        return this.isCloseBracket;
    }

    public int getEndingNumber() {
        return this.endingNumber;
    }
}