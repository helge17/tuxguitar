/*
 * Created on 26-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.tuxguitar.song.models;

import app.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGMeasureHeader {
	public static final int TRIPLET_FEEL_NONE = 1;
	public static final int TRIPLET_FEEL_EIGHTH = 2;
	public static final int TRIPLET_FEEL_SIXTEENTH = 3;

	private int number;
	/*
	 * measureHeader start can be defined under 2 different formats:
	 * - start
	 * - preciseStart
	 * for measure headers there is a bijection between these values, no possible rounding error
	 * since all measure durations are a multiple of a base duration (power of 2)
	 * None of these values consider repeats. They only consider previous measures (i.e. measures with lower measure numbers) and beats
	 */
	private long start;
	private long preciseStart;
	private TGTimeSignature timeSignature;
	private TGTempo tempo;
	private TGMarker marker;
	private boolean repeatOpen;
	private int repeatAlternative;
	private int repeatClose;
	private int tripletFeel;
	private TGSong song;
	private boolean lineBreak;

	public TGMeasureHeader(TGFactory factory){
		this.number = 0;
		this.preciseStart = TGDuration.getPreciseStartingPoint();
		this.start = TGDuration.getStartingPoint();
		this.timeSignature = factory.newTimeSignature();
		this.tempo = factory.newTempo();
		this.marker = null;
		this.tripletFeel = TRIPLET_FEEL_NONE;
		this.repeatOpen = false;
		this.repeatClose = 0;
		this.repeatAlternative = 0;
		this.checkMarker();
	}

	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
		this.checkMarker();
	}

	public int getRepeatClose() {
		return this.repeatClose;
	}

	public void setRepeatClose(int repeatClose) {
		this.repeatClose = repeatClose;
	}

	public int getRepeatAlternative() {
		return this.repeatAlternative;
	}

	/**
	 * bitwise value 1 TO 8.
	 * (1 << AlternativeNumber)
	 */
	public void setRepeatAlternative(int repeatAlternative) {
		this.repeatAlternative = repeatAlternative;
	}

	public boolean isRepeatOpen() {
		return this.repeatOpen;
	}

	public void setRepeatOpen(boolean repeatOpen) {
		this.repeatOpen = repeatOpen;
	}

	public long getStart() {
		return this.start;
	}

	public long getPreciseStart() {
		return this.preciseStart;
	}

	public void setStart(long start) {
		this.start = start;
		// preciseStart can be deduced from start, no possible rounding error here,
		// as all measures duration correctly divide value used for tick definition
		// (time signature denominator is a power of 2)
		this.start = start;
		this.preciseStart = TGDuration.toPreciseTime(start);
	}

	public void setPreciseStart(long pStart) {
		this.preciseStart = pStart;
		this.start = TGDuration.toTime(pStart);
	}

	public int getTripletFeel() {
		return this.tripletFeel;
	}

	public void setTripletFeel(int tripletFeel) {
		this.tripletFeel = tripletFeel;
	}

	public TGTempo getTempo() {
		return this.tempo;
	}

	public void setTempo(TGTempo tempo) {
		this.tempo = tempo;
	}

	public TGTimeSignature getTimeSignature() {
		return this.timeSignature;
	}

	public void setTimeSignature(TGTimeSignature timeSignature) {
		this.timeSignature = timeSignature;
	}

	public TGMarker getMarker() {
		return this.marker;
	}

	public void setMarker(TGMarker marker) {
		this.marker = marker;
	}

	public boolean hasMarker(){
		return (getMarker() != null);
	}

	private void checkMarker(){
		if(hasMarker()){
			this.marker.setMeasure(getNumber());
		}
	}

	public long getLength(){
		return getTimeSignature().getNumerator() * getTimeSignature().getDenominator().getTime();
	}

	public long getPreciseLength() {
		return getTimeSignature().getNumerator() * TGDuration.WHOLE_PRECISE_DURATION / getTimeSignature().getDenominator().getValue();
	}

	public TGSong getSong() {
		return this.song;
	}

	public void setSong(TGSong song) {
		this.song = song;
	}

	public void toggleLineBreak() {
		this.lineBreak = !this.lineBreak;
	}

	public boolean isLineBreak() {
		return this.lineBreak;
	}

	public void setLineBreak(boolean lineBreak) {
		this.lineBreak = lineBreak;
	}


	public void copyFrom(TGFactory factory, TGMeasureHeader header){
		this.setNumber(header.getNumber());
		this.setStart(header.getStart());
		this.setRepeatOpen(header.isRepeatOpen());
		this.setRepeatAlternative(header.getRepeatAlternative());
		this.setRepeatClose(header.getRepeatClose());
		this.setTripletFeel(header.getTripletFeel());
		this.getTimeSignature().copyFrom(header.getTimeSignature());
		this.getTempo().copyFrom(header.getTempo());
		this.setMarker(header.hasMarker() ? header.getMarker().clone(factory) : null);
		this.checkMarker();
		this.setLineBreak(header.isLineBreak());

	}

	public TGMeasureHeader clone(TGFactory factory){
		TGMeasureHeader tgMeasureHeader = factory.newHeader();
		tgMeasureHeader.copyFrom(factory, this);
		return tgMeasureHeader;
	}

}
