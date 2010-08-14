package org.herac.tuxguitar.io.gpx.score;

public class GPXVoice extends GPXDocumentElement {
	
	private int id;
	private int[] beatIds;
	
	public GPXVoice(GPXDocument document){
		super(document);
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int[] getBeatIds() {
		return this.beatIds;
	}
	
	public void setBeatIds(int[] beatIds) {
		this.beatIds = beatIds;
	}
}
