package org.herac.tuxguitar.io.ptb.base;

public class PTNote{
	private int value;
	private int string;
	private int bend;
	private boolean tied;
	private boolean dead;
	private boolean hammer;
	private boolean slide;
	
	public PTNote(){
		super();
	}
	
	public int getString() {
		return this.string;
	}
	
	public void setString(int string) {
		this.string = string;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public boolean isDead() {
		return this.dead;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public boolean isTied() {
		return this.tied;
	}
	
	public void setTied(boolean tied) {
		this.tied = tied;
	}
	
	public int getBend() {
		return this.bend;
	}
	
	public void setBend(int bend) {
		this.bend = bend;
	}
	
	public boolean isHammer() {
		return this.hammer;
	}
	
	public void setHammer(boolean hammer) {
		this.hammer = hammer;
	}
	
	public boolean isSlide() {
		return this.slide;
	}
	
	public void setSlide(boolean slide) {
		this.slide = slide;
	}
	
	public PTNote getClone(){
		PTNote note = new PTNote();
		note.setValue( getValue() );
		note.setString( getString() );
		note.setTied( isTied() );
		note.setDead( isDead() );
		note.setBend( getBend() );
		note.setHammer( isHammer() );
		note.setSlide( isSlide() );
		return note;
	}
}