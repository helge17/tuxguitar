package org.herac.tuxguitar.io.ptb.base;

public class PTBar implements PTComponent{
	private int repeatClose;
	private boolean repeatStart;
	
	private int numerator;
	private int denominator;
	
	public PTBar(){
		super();
	}
	
	public int getDenominator() {
		return this.denominator;
	}
	
	public void setDenominator(int denominator) {
		this.denominator = denominator;
	}
	
	public int getNumerator() {
		return this.numerator;
	}
	
	public void setNumerator(int numerator) {
		this.numerator = numerator;
	}
	
	public int getRepeatClose() {
		return this.repeatClose;
	}
	
	public void setRepeatClose(int repeatClose) {
		this.repeatClose = repeatClose;
	}
	
	public boolean isRepeatStart() {
		return this.repeatStart;
	}
	
	public void setRepeatStart(boolean repeatStart) {
		this.repeatStart = repeatStart;
	}
	
	public PTComponent getClone(){
		PTBar bar = new PTBar();
		bar.setNumerator( getNumerator() );
		bar.setDenominator( getDenominator() );
		bar.setRepeatStart( isRepeatStart() );
		bar.setRepeatClose( getRepeatClose() );
		return bar;
	}
}