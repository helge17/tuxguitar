/*
 * Created on 29-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGTimeSignature {
	private TGDuration denominator;
	private int numerator;
	
	public TGTimeSignature(TGFactory factory){
		this.numerator = 4;
		this.denominator = factory.newDuration();
	}
	
	public int getNumerator() {
		return this.numerator;
	}
	
	public void setNumerator(int numerator) {
		this.numerator = numerator;
	}
	
	public TGDuration getDenominator() {
		return this.denominator;
	}
	
	public void setDenominator(TGDuration denominator) {
		this.denominator = denominator;
	}
	
	public TGTimeSignature clone(TGFactory factory){
		TGTimeSignature tgTimeSignature = factory.newTimeSignature();
		tgTimeSignature.copyFrom(this);
		return tgTimeSignature;
	}
	
	public void copyFrom(TGTimeSignature timeSignature){
		this.setNumerator(timeSignature.getNumerator());
		this.getDenominator().copyFrom(timeSignature.getDenominator());
	}
	
	public boolean isEqual(TGTimeSignature ts){
		return (getNumerator() == ts.getNumerator() && getDenominator().isEqual(ts.getDenominator()));
	}
}
