package org.herac.tuxguitar.io.ptb.base;

public class PTTrackInfo {
	private String name;
	private int number;
	private int instrument;
	private int volume;
	private int balance;
	private int reverb;
	private int chorus;
	private int tremolo;
	private int phaser;
	
	private int[] strings;
	
	public PTTrackInfo(){
		super();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getBalance() {
		return this.balance;
	}
	
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	public int getChorus() {
		return this.chorus;
	}
	
	public void setChorus(int chorus) {
		this.chorus = chorus;
	}
	
	public int getInstrument() {
		return this.instrument;
	}
	
	public void setInstrument(int instrument) {
		this.instrument = instrument;
	}
	
	public int getPhaser() {
		return this.phaser;
	}
	
	public void setPhaser(int phaser) {
		this.phaser = phaser;
	}
	
	public int getReverb() {
		return this.reverb;
	}
	
	public void setReverb(int reverb) {
		this.reverb = reverb;
	}
	
	public int getTremolo() {
		return this.tremolo;
	}
	
	public void setTremolo(int tremolo) {
		this.tremolo = tremolo;
	}
	
	public int getVolume() {
		return this.volume;
	}
	
	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	public int[] getStrings(){
		return this.strings;
	}
	
	public void setStrings(int[] strings){
		this.strings = strings;
	}
	
	public PTTrackInfo getClone(){
		int[] strings = new int[ this.strings.length ] ;
		for( int i = 0 ; i < strings.length ; i ++ ){
			strings[ i ] = this.strings[ i ];
		}
		PTTrackInfo info = new PTTrackInfo();
		info.setNumber( getNumber() );
		info.setName( getName() );
		info.setInstrument( getInstrument() );
		info.setVolume( getVolume() );
		info.setBalance( getBalance() );
		info.setChorus( getChorus() );
		info.setPhaser( getPhaser() );
		info.setReverb( getReverb() );
		info.setTremolo( getTremolo() );
		info.setStrings( strings );
		
		return info;
	}
}
