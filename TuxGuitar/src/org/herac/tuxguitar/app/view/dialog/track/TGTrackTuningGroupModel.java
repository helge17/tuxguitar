package org.herac.tuxguitar.app.view.dialog.track;

public class TGTrackTuningGroupModel {

    private String name;
    private TGTrackTuningGroupEntryModel[] children;
    private TGTrackTuningGroupEntryModel entry;

    public TGTrackTuningGroupEntryModel[] getChildren() {
        return children;
    }

    public void setChildren(TGTrackTuningGroupEntryModel[] children) {
        this.children = children;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TGTrackTuningGroupEntryModel getEntry() {
        return entry;
    }

    public void setEntry(TGTrackTuningGroupEntryModel entry) {
        this.entry = entry;
    }
}
