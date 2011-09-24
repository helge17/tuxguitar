package org.herac.tuxguitar.io.gpx.score;

public class GPXBeat {
	
	private int id;
	private int rhythmId;
	private int[] noteIds;
	private String dynamic;
	private boolean slapped;
	private boolean popped;
	private String brush;	// up stroke, down stroke
	private int[] tremolo; // 1/8, 1/4, etc.
	private boolean FadeIn;
	private boolean FadeOut;
	private String text;
	
	public GPXBeat(){
		this.slapped = false;
		this.popped = false;
		this.tremolo = null;
		this.FadeIn = false;
		this.FadeOut = false;
		this.brush = new String();
		this.text = new String();
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

	public boolean isSlapped() {
		return slapped;
	}

	public void setSlapped(boolean slapped) {
		this.slapped = slapped;
	}

	public boolean isPopped() {
		return popped;
	}

	public void setPopped(boolean popped) {
		this.popped = popped;
	}

	public String getBrush() {
		return brush;
	}

	public void setBrush(String brush) {
		this.brush = brush;
	}

	public int[] getTremolo() {
		return tremolo;
	}

	public void setTremolo(int[] tremolo) {
		this.tremolo = tremolo;
	}

	public boolean isFadeIn() {
		return FadeIn;
	}

	public void setFadeIn(boolean fadeIn) {
		FadeIn = fadeIn;
	}

	public boolean isFadeOut() {
		return FadeOut;
	}

	public void setFadeOut(boolean fadeOut) {
		FadeOut = fadeOut;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
