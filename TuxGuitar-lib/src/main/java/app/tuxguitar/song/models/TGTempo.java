/*
 * Created on 29-nov-2005
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
public abstract class TGTempo {
	private static final int SECOND_IN_MILLIS = 1000;

	private int quarterValue;		// nb of quarters per minute
	private int rawValue;	// nb of base duration per minute
	private int base;
	private boolean dotted;

	public TGTempo(){
		this.quarterValue = 120;
		this.rawValue = this.quarterValue;
		this.base = TGDuration.QUARTER;
		this.dotted = false;
	}

	public int getQuarterValue() {
		return this.quarterValue;
	}

	public void setQuarterValue(int value) {
		this.setValueBase(value, TGDuration.QUARTER, false);
	}

	public void setValueBase(int rawValue, int base, boolean dotted) {
		this.quarterValue = rawValue * TGDuration.QUARTER / base;
		if (dotted) {
			this.quarterValue = 3*this.quarterValue/2;
		}
		this.rawValue = rawValue;
		this.base = base;
		this.dotted = dotted;
	}

	public int getRawValue() {
		return this.rawValue;
	}

	public int getBase() {
		return this.base;
	}

	public boolean isDotted() {
		return this.dotted;
	}

	public long getInMillis(){
		double millis = (60.00 / getQuarterValue() * SECOND_IN_MILLIS);
		return (long)millis;
	}

	public long getTicksInMillis(long ticks) {
		return 60l*1000l*ticks / TGDuration.QUARTER_TIME / getQuarterValue();
	}

	public long getInUSQ(){
		double usq = ((60.00 / getQuarterValue() * SECOND_IN_MILLIS) * 1000.00);
		return (long)usq;
	}

	public static TGTempo fromUSQ(TGFactory factory,int usq){
		double value = ((60.00 * SECOND_IN_MILLIS) / (usq / 1000.00));
		TGTempo tempo = factory.newTempo();
		tempo.setValueBase((int)value, TGDuration.QUARTER, false);
		return tempo;
	}

	public TGTempo clone(TGFactory factory){
		TGTempo tgTempo = factory.newTempo();
		tgTempo.copyFrom(this);
		return tgTempo;
	}

	public void copyFrom(TGTempo tempo){
		this.setValueBase(tempo.getRawValue(), tempo.getBase(), tempo.isDotted());
	}

	public boolean isEqual(TGTempo other) {
		return ( (other.rawValue == this.rawValue)
				&& (other.base == this.base)
				&& (other.dotted == this.dotted));
	}
}
