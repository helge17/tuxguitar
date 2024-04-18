package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.models.TGMeasureHeader;

public class TGMeasuresInterval {
	private String title;
	private TGMeasureHeader start;
	private TGMeasureHeader end;
	
	public TGMeasuresInterval() {
		this.title = "";
		this.start = null;
		this.end = null;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setStart(TGMeasureHeader start) {
		this.start = start;
	}

	public void setEnd(TGMeasureHeader end) {
		this.end = end;
	}

	public String getTitle() {
		return this.title;
	}

	public TGMeasureHeader getStart() {
		return this.start;
	}

	public TGMeasureHeader getEnd() {
		return this.end;
	}
}


