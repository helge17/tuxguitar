package app.tuxguitar.io.tef3.base;

public class TELyrics {
    private int trackNumber;
    private TEFontPreset fontPreset;
    private int yPosition;
    private String lyrics;

    public TELyrics(int trackNumber, TEFontPreset fontPreset, int yPosition, String lyrics) {
        this.trackNumber = trackNumber;
        this.fontPreset = fontPreset;
        this.yPosition = yPosition;
        this.lyrics = lyrics;
    }

    public int getTrackNumber() {
        return this.trackNumber;
    }

    public TEFontPreset getFontPreset() {
        return this.fontPreset;
    }

    public int getYposition() {
        return this.yPosition;
    }

    public String getLyrics() {
        return this.lyrics;
    }
}
