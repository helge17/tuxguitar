package org.herac.tuxguitar.io.abc.base;

public class ABCTempoChange {
	private int measure;
	private int tempo;
	
	public ABCTempoChange(int measure, int tempo) {
		this.measure = measure;
		this.tempo = tempo;
	}
	
	public int getMeasure(){
		return this.measure;
	}
	
	public int getTempo() {
		return this.tempo;
	}
	
	public String toString(){
		String string = new String("[TEMPO CHANGE]");
		string += "\n     Measure:     " + getMeasure();
		string += "\n     Value:       " + getTempo();
		return string;
	}

}
