package org.herac.tuxguitar.io.ptb.base;

public class PTTempo implements PTComponent{
	private int tempo;
	private int tripletFeel;
	
	public PTTempo(int tempo,int tripletFeel){
		this.tempo = tempo;
		this.tripletFeel = tripletFeel;
	}
	
	public int getTempo() {
		return this.tempo;
	}
	
	public int getTripletFeel() {
		return this.tripletFeel;
	}
	
	public PTComponent getClone(){
		return new PTTempo( getTempo() , getTripletFeel() );
	}
}
