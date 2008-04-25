package org.herac.tuxguitar.io.tef.base;

public class TEPercussion {
	
	private String name;
	private int volume;
	
	public TEPercussion(String name, int volume) {
		this.name = name;
		this.volume = volume;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getVolume() {
		return this.volume;
	}
	
	public String toString(){
		String string = new String("[PERCUSSION]");
		string += "\n     Name:       " + getName();
		string += "\n     Volume:     " + getVolume();
		return string;
	}
}
