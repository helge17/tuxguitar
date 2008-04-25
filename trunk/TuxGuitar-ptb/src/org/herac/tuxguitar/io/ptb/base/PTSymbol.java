package org.herac.tuxguitar.io.ptb.base;

public class PTSymbol implements PTComponent{
	
	private int endNumber;
	
	public PTSymbol(){
		super();
	}
	
	public int getEndNumber() {
		return this.endNumber;
	}
	
	public void setEndNumber(int endNumber) {
		this.endNumber = endNumber;
	}
	
	public PTComponent getClone(){
		PTSymbol symbol = new PTSymbol();
		symbol.setEndNumber( getEndNumber() );
		return symbol;
	}
}
