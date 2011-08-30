package org.herac.tuxguitar.io.abc.base;

public class ABCTimeSignature {
	
	private int numerator;
	private int denominator;
	private boolean toEnd;
	
	public ABCTimeSignature(int numerator, int denominator, boolean toEnd) {
		this.numerator = numerator;
		this.denominator = denominator;
		this.toEnd=toEnd;
	}
	
	public String toString() {
		return this.numerator+"/"+this.denominator;
	}
	
	public int getDenominator() {
		return this.denominator;
	}
	
	public int getNumerator() {
		return this.numerator;
	}

	/**
	 * @return the toEnd
	 */
	public boolean isToEnd() {
		return toEnd;
	}
}
