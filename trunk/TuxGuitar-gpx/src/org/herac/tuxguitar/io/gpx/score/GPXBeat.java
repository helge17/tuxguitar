package org.herac.tuxguitar.io.gpx.score;

public class GPXBeat {
	
	private int id;
	private int rhythmId;
	private int[] noteIds;
	private String dynamic;
	
	public GPXBeat(){
		super();
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getRhythmId() {
		return this.rhythmId;
	}
	
	public void setRhythmId(int rhythmId) {
		this.rhythmId = rhythmId;
	}
	
	public int[] getNoteIds() {
		return this.noteIds;
	}
	
	public void setNoteIds(int[] noteIds) {
		this.noteIds = noteIds;
	}
	
	public String getDynamic() {
		return this.dynamic;
	}
	
	public void setDynamic(String dynamic) {
		this.dynamic = dynamic;
	}
}
