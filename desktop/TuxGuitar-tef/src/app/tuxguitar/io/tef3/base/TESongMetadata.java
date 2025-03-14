package app.tuxguitar.io.tef3.base;

import java.util.List;

public class TESongMetadata {
    private String songTitle;
    private String authorName;
    private String comments;
    private String notes;
    private String url;
    private String copyright;
    private List<TELyrics> lyrics;
    private List<String> textEvents;

    public TESongMetadata(String songTitle, String authorName, String comments, String notes, String url, String copyright, List<TELyrics> lyrics, List<String> textEvents) {
        this.songTitle = songTitle;
        this.authorName = authorName;
        this.comments = comments;
        this.notes = notes;
        this.url = url;
        this.copyright = copyright;
        this.lyrics = lyrics;
        this.textEvents = textEvents;
    }

    public String getSongTitle() {
        return this.songTitle;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public String getComments() {
        return this.comments;
    }

    public String getNotes() {
        return this.notes;
    }

    public String getUrl() {
        return this.url;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public List<TELyrics> getLyrics() {
        return this.lyrics;
    }

    public List<String> getTextEvents() {
        return this.textEvents;
    }
}
