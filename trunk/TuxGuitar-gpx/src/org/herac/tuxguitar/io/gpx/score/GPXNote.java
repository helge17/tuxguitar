package org.herac.tuxguitar.io.gpx.score;

public class GPXNote {
	
	private int id;
	private int fret;
	private int string;
	private int tone;
	private int octave;
	private int element;
	private int trill;
	private int variation;
	private int midiNumber;
	// bends are measured in % of full step.  100 = full step, not 100 (cents) = half step
	private boolean bendEnabled;
	private int bendOriginValue;
	private int bendMiddleValue;
	private int bendDestinationValue;

	private boolean hammer;
	private boolean ghost;
	private boolean slide;
	private int slideFlags;	// 1, 2, 4, 8, 16 - 2 seems to be up, 4 seems to be down.
	private boolean vibrato;
	private int accent;
	private boolean tapped;
	private boolean tieDestination;
	private boolean mutedEnabled;
	private boolean palmMutedEnabled;
	
	private int harmonicFret;
	private String harmonicType;

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
		this.hammer = false;
		this.ghost = false;
		this.trill = 0;
		this.accent = 0;
		this.bendEnabled = false;
		this.bendOriginValue = 0;
		this.bendMiddleValue = 0;
		this.bendDestinationValue = 0;
		this.slideFlags = 0;
		this.harmonicType = "";
		this.harmonicFret = -1;
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

	public int getTrill() {
		return trill;
	}

	public void setTrill(int trill) {
		this.trill = trill;
	}

	public boolean isBendEnabled() {
		return bendEnabled;
	}

	public void setBendEnabled(boolean bendEnabled) {
		this.bendEnabled = bendEnabled;
	}

	public int getBendOriginValue() {
		return bendOriginValue;
	}

	public void setBendOriginValue(int bendOriginValue) {
		this.bendOriginValue = bendOriginValue;
	}

	public int getBendMiddleValue() {
		return bendMiddleValue;
	}

	public void setBendMiddleValue(int bendMiddleValue) {
		this.bendMiddleValue = bendMiddleValue;
	}

	public int getBendDestinationValue() {
		return bendDestinationValue;
	}

	public void setBendDestinationValue(int bendDestinationValue) {
		this.bendDestinationValue = bendDestinationValue;
	}

	public boolean isHammer() {
		return hammer;
	}

	public void setHammer(boolean hammer) {
		this.hammer = hammer;
	}

	public boolean isGhost() {
		return ghost;
	}

	public void setGhost(boolean ghost) {
		this.ghost = ghost;
	}

	public int getSlideFlags() {
		return slideFlags;
	}

	public void setSlideFlags(int slideFlags) {
		this.slideFlags = slideFlags;
	}

	public int getAccent() {
		return accent;
	}

	public void setAccent(int accent) {
		this.accent = accent;
	}

	public boolean isTapped() {
		return tapped;
	}

	public void setTapped(boolean tapped) {
		this.tapped = tapped;
	}
	
	public int getHarmonicFret() {
		return harmonicFret;
	}

	public void setHarmonicFret(int harmonicFret) {
		this.harmonicFret = harmonicFret;
	}

	public String getHarmonicType() {
		return harmonicType;
	}

	public void setHarmonicType(String harmonicType) {
		this.harmonicType = harmonicType;
	}
}
