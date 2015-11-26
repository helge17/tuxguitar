package org.herac.tuxguitar.android.drawer.main;

import org.herac.tuxguitar.song.models.TGTrack;

public class TGMainDrawerTrackListItem {

	private TGTrack track;
	private String label;
	private Boolean selected;
	
	public TGMainDrawerTrackListItem() {
		super();
	}

	public TGTrack getTrack() {
		return track;
	}

	public void setTrack(TGTrack track) {
		this.track = track;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
