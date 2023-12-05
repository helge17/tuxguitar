package org.herac.tuxguitar.io.tef.base;

public class TETrack {
	
	public static final int FLAG_DOUBLE_STRINGS = 0x01;
	
	public static final int FLAG_LET_RING = 0x02;
	
	public static final int FLAG_PEDAL_STEEL_GUITAR = 0x04;
	
	public static final int FLAG_EFFECT_CHANNEL_DISABLED = 0x08;
	
	public static final int FLAG_RHYTHM_TRACK = 0x10;
	
	private boolean percussion;
	
	private int instrument;
	
	private int capo;
	
	private int clefType;
	
	private int clefNumber;
	
	private int pan;
	
	private int volume;
	
	private int flags;
	
	private int[] strings;
	
	private String name;
	
	public TETrack(boolean percussion,int instrument, int capo, int clefType, int clefNumber, int pan, int volume, int flags, int[] strings, String name) {
		this.percussion = percussion;
		this.instrument = instrument;
		this.capo = capo;
		this.clefType = clefType;
		this.clefNumber = clefNumber;
		this.pan = pan;
		this.volume = volume;
		this.flags = flags;
		this.strings = strings;
		this.name = name;
	}
	
	public int getCapo() {
		return this.capo;
	}
	
	public int getClefNumber() {
		return this.clefNumber;
	}
	
	public int getClefType() {
		return this.clefType;
	}
	
	public int getFlags() {
		return this.flags;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getPan() {
		return this.pan;
	}
	
	public boolean isPercussion() {
		return this.percussion;
	}
	
	public int[] getStrings() {
		return this.strings;
	}
	
	public int getVolume() {
		return this.volume;
	}
	
	public int getInstrument() {
		return this.instrument;
	}
	
	public String toString(){
		String string = new String("[TRACK]");
		string += "\n     "  + this.getName();
		
		for(int i = 0; i < this.strings.length; i ++){
			string += "\n     String " + i + ": " + (96 - this.strings[i]);
		}
		
		return string;
	}
}
