package org.herac.tuxguitar.gui.editors.chord;

public class ChordSettings {
	
	private static ChordSettings instance;
	
	private boolean emptyStringChords;
	private float bassGrade;
	private float fingeringGrade;
	private float subsequentGrade;
	private float requiredBasicsGrade;
	private float manyStringsGrade;
	private float goodChordSemanticsGrade;
	private int chordsToDisplay;
	private int howManyIncompleteChords;
	private int chordTypeIndex;
	private int findChordsMin;
	private int findChordsMax;
	
	private ChordSettings() {
		this.emptyStringChords = false;
		this.bassGrade = 200.0f;
		this.fingeringGrade = 150.0f; // was:200
		this.subsequentGrade = 200.0f;
		this.requiredBasicsGrade = 150.0f;
		this.manyStringsGrade = 100.0f;
		this.goodChordSemanticsGrade = 200.0f;
		this.chordsToDisplay = 30;
		this.howManyIncompleteChords = 4;
		this.chordTypeIndex = 0;
		this.findChordsMin=0;
		this.findChordsMax=15;
	}
	
	public static ChordSettings instance(){
		if( instance == null ){
			instance = new ChordSettings();
		}
		return instance;
	}
	
	public float getBassGrade() {
		return this.bassGrade;
	}
	
	public void setBassGrade(float bassGrade) {
		this.bassGrade = bassGrade;
	}
	
	public int getChordsToDisplay() {
		return this.chordsToDisplay;
	}
	
	public void setChordsToDisplay(int chordsToDisplay) {
		this.chordsToDisplay = chordsToDisplay;
	}
	
	public boolean isEmptyStringChords() {
		return this.emptyStringChords;
	}
	
	public void setEmptyStringChords(boolean emptyStringChords) {
		this.emptyStringChords = emptyStringChords;
	}
	
	public float getFingeringGrade() {
		return this.fingeringGrade;
	}
	
	public void setFingeringGrade(float fingeringGrade) {
		this.fingeringGrade = fingeringGrade;
	}
	
	public float getGoodChordSemanticsGrade() {
		return this.goodChordSemanticsGrade;
	}
	
	public void setGoodChordSemanticsGrade(float goodChordSemanticsGrade) {
		this.goodChordSemanticsGrade = goodChordSemanticsGrade;
	}
	
	public float getManyStringsGrade() {
		return this.manyStringsGrade;
	}
	
	public void setManyStringsGrade(float manyStringsGrade) {
		this.manyStringsGrade = manyStringsGrade;
	}
	
	public float getRequiredBasicsGrade() {
		return this.requiredBasicsGrade;
	}
	
	public void setRequiredBasicsGrade(float requiredBasicsGrade) {
		this.requiredBasicsGrade = requiredBasicsGrade;
	}
	
	public float getSubsequentGrade() {
		return this.subsequentGrade;
	}
	
	public void setSubsequentGrade(float subsequentGrade) {
		this.subsequentGrade = subsequentGrade;
	}
	
	public int getIncompleteChords() {
		return this.howManyIncompleteChords;
	}
	
	public void setIncompleteChords(int incomplete) {
		this.howManyIncompleteChords = incomplete;
	}
	
	public int getFindChordsMin() {
		return this.findChordsMin;
	}
	
	public void setFindChordsMin(int min) {
		this.findChordsMin = min;
	}
	
	public int getFindChordsMax() {
		return this.findChordsMax;
	}
	
	public void setFindChordsMax(int max) {
		this.findChordsMax = max;
	}
	
	public int getChordTypeIndex() {
		return this.chordTypeIndex;
	}
	
	public void setChordTypeIndex(int index) {
		switch (index) {
			case 0 : // normal
					this.bassGrade = 200.0f;
					this.fingeringGrade = 150.0f;
					this.subsequentGrade = 200.0f;
					this.requiredBasicsGrade = 150.0f;
					this.manyStringsGrade = 100.0f;
					this.goodChordSemanticsGrade = 200.0f;
				    break;
			case 1 : // inversions
					this.bassGrade = -100.0f;
					this.fingeringGrade = 150.0f;
					this.subsequentGrade = 200.0f;
					this.requiredBasicsGrade = 150.0f;
					this.manyStringsGrade = 50.0f;
					this.goodChordSemanticsGrade = 200.0f;
					break;
			case 2 : // close-voiced
					this.bassGrade = 50.0f;
					this.fingeringGrade = 200.0f;
					this.subsequentGrade = 350.0f;
					this.requiredBasicsGrade = 150.0f;
					this.manyStringsGrade = -100.0f;
					this.goodChordSemanticsGrade = 200.0f;
					break;
			case 3 : // open-voiced
					this.bassGrade = 100.0f;
					this.fingeringGrade = 100.0f;
					this.subsequentGrade = -80.0f;
					this.requiredBasicsGrade = 100.0f;
					this.manyStringsGrade = -80.0f;
					this.goodChordSemanticsGrade = 200.0f;
					break;
		}
		this.chordTypeIndex = index;
	}
}
