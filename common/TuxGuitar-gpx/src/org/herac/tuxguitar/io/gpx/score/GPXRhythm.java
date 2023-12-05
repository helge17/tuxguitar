package org.herac.tuxguitar.io.gpx.score;

public class GPXRhythm {
	
	private int id;
	private int augmentationDotCount;
	private int primaryTupletNum;
	private int primaryTupletDen;
	private String noteValue;
	
	public GPXRhythm(){
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNoteValue() {
		return noteValue;
	}

	public void setNoteValue(String noteValue) {
		this.noteValue = noteValue;
	}

	public int getAugmentationDotCount() {
		return augmentationDotCount;
	}

	public void setAugmentationDotCount(int augmentationDotCount) {
		this.augmentationDotCount = augmentationDotCount;
	}

	public int getPrimaryTupletNum() {
		return primaryTupletNum;
	}

	public void setPrimaryTupletNum(int primaryTupletNum) {
		this.primaryTupletNum = primaryTupletNum;
	}

	public int getPrimaryTupletDen() {
		return primaryTupletDen;
	}

	public void setPrimaryTupletDen(int primaryTupletDen) {
		this.primaryTupletDen = primaryTupletDen;
	}
}
