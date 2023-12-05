package org.herac.tuxguitar.io.abc.base;

public class ABCTimeSignatureChange{
	
	private int measure;
	private ABCTimeSignature timeSignature;
	
	public ABCTimeSignatureChange(int measure, ABCTimeSignature timeSignature) {
		this.measure = measure;
		this.timeSignature = timeSignature;
	}
	
	public int getMeasure(){
		return this.measure;
	}
	
	public ABCTimeSignature getTimeSignature() {
		return this.timeSignature;
	}
	
	public String toString(){
		String string = new String("[TIME SIGNATURE CHANGE]");
		string += "\n     Measure:     " + getMeasure();
		string += "\n     Numerator:   " + getTimeSignature().getNumerator();
		string += "\n     Denominator: " + getTimeSignature().getDenominator();
		return string;
	}
}
