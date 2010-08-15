package org.herac.tuxguitar.io.gpx.score;

public class GPXNote {
	
	private int id;
	private int fret;
	private int string;
	private int tone;
	private int octave;
	private int element;
	private int variation;
	private int midiNumber;
	
	private boolean slide;
	private boolean vibrato;
	private boolean tieDestination;
	private boolean mutedEnabled;
	private boolean palmMutedEnabled;
	
	public GPXNote(){
		super();
		this.id = -1;
		this.fret = -1;
		this.string = -1;
		this.tone = -1;
		this.octave = -1;
		this.element = -1;
		this.variation = -1;
		this.midiNumber = -1;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getFret() {
		return this.fret;
	}
	
	public void setFret(int fret) {
		this.fret = fret;
	}
	
	public int getString() {
		return this.string;
	}
	
	public void setString(int string) {
		this.string = string;
	}
	
	public int getTone() {
		return tone;
	}
	
	public void setTone(int tone) {
		this.tone = tone;
	}
	
	public int getOctave() {
		return octave;
	}
	
	public void setOctave(int octave) {
		this.octave = octave;
	}
	
	public boolean isTieDestination() {
		return tieDestination;
	}
	
	public void setTieDestination(boolean tieDestination) {
		this.tieDestination = tieDestination;
	}

	public boolean isMutedEnabled() {
		return mutedEnabled;
	}

	public void setMutedEnabled(boolean mutedEnabled) {
		this.mutedEnabled = mutedEnabled;
	}

	public boolean isPalmMutedEnabled() {
		return palmMutedEnabled;
	}

	public void setPalmMutedEnabled(boolean palmMutedEnabled) {
		this.palmMutedEnabled = palmMutedEnabled;
	}

	public boolean isSlide() {
		return slide;
	}

	public void setSlide(boolean slide) {
		this.slide = slide;
	}

	public boolean isVibrato() {
		return vibrato;
	}

	public void setVibrato(boolean vibrato) {
		this.vibrato = vibrato;
	}

	public int getElement() {
		return element;
	}

	public void setElement(int element) {
		this.element = element;
	}

	public int getVariation() {
		return variation;
	}

	public void setVariation(int variation) {
		this.variation = variation;
	}

	public int getMidiNumber() {
		return midiNumber;
	}

	public void setMidiNumber(int midiNumber) {
		this.midiNumber = midiNumber;
	}
	
}
