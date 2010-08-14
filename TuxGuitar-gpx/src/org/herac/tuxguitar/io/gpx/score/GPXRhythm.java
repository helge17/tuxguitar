package org.herac.tuxguitar.io.gpx.score;

public class GPXRhythm extends GPXDocumentElement {
	
	
/*

Whole
Half
Quarter
Eighth
16th
32nd
64th
*/
	private int id;
	private int augmentationDotCount;
	private int primaryTupletNum;
	private int primaryTupletDen;
	private String noteValue;
	
	public GPXRhythm(GPXDocument document){
		super(document);
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
