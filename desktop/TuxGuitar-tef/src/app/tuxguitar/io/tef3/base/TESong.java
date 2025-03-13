package app.tuxguitar.io.tef3.base;

import java.util.List;

public class TESong {
    private TEFileMetadata fileMetadata;
    private TESongMetadata songMetadata;
    private List<TEChordDefinition> chordDefinitions;
    private List<TEMeasure> measures;
    private List<TETrack> tracks;
    private int totalStringCount;
    private TEPrintMetadata printMetadata;
    private List<TEReadingListEntry> readingListEntries;
    private List<TEComponentBase> components;

    public TESong()
    {
        this.fileMetadata = null;
        this.songMetadata = null;
        this.chordDefinitions = null;
        this.measures = null;
        this.tracks = null;
        this.totalStringCount = 0;
        this.printMetadata = null;
        this.readingListEntries = null;
        this.components = null;
    }

    public TEFileMetadata getFileMetadata() {
        return this.fileMetadata;
    }

    public void setFileMetadata(TEFileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
    }

    public TESongMetadata getSongMetadata() {
        return this.songMetadata;
    }

    public void setSongMetadata(TESongMetadata songMetadata) {
        this.songMetadata = songMetadata;
    }

    public List<TEChordDefinition> getChordDefinitions() {
        return this.chordDefinitions;
    }

    public void setChordDefinitions(List<TEChordDefinition> chordDefinitions) {
        this.chordDefinitions = chordDefinitions;
    }

    public List<TEMeasure> getMeasures() {
        return this.measures;
    }

    public void setMeasures(List<TEMeasure> measures) {
        this.measures = measures;
    }

    public List<TETrack> getTracks() {
        return this.tracks;
    }

    public void setTracks(List<TETrack> tracks) {
        this.tracks = tracks;
    }

    public int getTotalStringCount() {
        return this.totalStringCount;
    }

    public void setTotalStringCount(int totalStringCount) {
        this.totalStringCount = totalStringCount;
    }

    public TEPrintMetadata getPrintMetadata() {
        return this.printMetadata;
    }

    public void setPrintMetadata(TEPrintMetadata printMetadata) {
        this.printMetadata = printMetadata;
    }

    public List<TEReadingListEntry> getReadingListEntries() {
        return this.readingListEntries;
    }

    public void setReadingListEntries(List<TEReadingListEntry> readingListEntries) {
        this.readingListEntries = readingListEntries;
    }

    public List<TEComponentBase> getComponents() {
        return this.components;
    }

    public void setComponents(List<TEComponentBase> components) {
        this.components = components;
    }
}
