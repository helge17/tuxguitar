package org.herac.tuxguitar.io.tef3.base;

public class TETimeSignature {
	
	private int numerator;
	private int denominator;
	
	public TETimeSignature(int numerator, int denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	public int getNumerator() {
		return this.numerator;
	}

	public int getDenominator() {
		return this.denominator;
	}
}
