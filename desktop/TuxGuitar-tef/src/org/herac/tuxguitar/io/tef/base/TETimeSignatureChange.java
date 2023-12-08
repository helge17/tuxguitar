package org.herac.tuxguitar.io.tef.base;

public class TETimeSignatureChange{
	
	private int measure;
	private TETimeSignature timeSignature;
	
	public TETimeSignatureChange(int measure, TETimeSignature timeSignature) {
		this.measure = measure;
		this.timeSignature = timeSignature;
	}
	
	public int getMeasure(){
		return this.measure;
	}
	
	public TETimeSignature getTimeSignature() {
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
