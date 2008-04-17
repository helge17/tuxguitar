package org.herac.tuxguitar.player.base;

public class MidiPlayerMode{
	
	public static final int DEFAULT_TEMPO_PERCENT = 100;
	
	public static final int TYPE_SINGLE = 1;
	public static final int TYPE_CUSTOM = 2;
	
	private int type;
	private boolean loop;
	private int singlePercent;
	private int customPercentFrom;
	private int customPercentTo;
	private int customPercentIncrement;
	
	private int currentPercent;
	
	public MidiPlayerMode(){
		this.loop = false;
		this.type = TYPE_SINGLE;
		this.singlePercent = DEFAULT_TEMPO_PERCENT;
		this.customPercentFrom = DEFAULT_TEMPO_PERCENT;
		this.customPercentTo = DEFAULT_TEMPO_PERCENT;
		this.customPercentIncrement = 0;
		this.reset();
	}
	
	public void reset(){
		if(getType() == TYPE_SINGLE){
			this.currentPercent = getSinglePercent();
		}
		else if(getType() == TYPE_CUSTOM){
			this.currentPercent = getCustomPercentFrom();
		}
	}
	
	public void notifyLoop(){
		if(getType() == TYPE_SINGLE){
			this.currentPercent = getSinglePercent();
		}
		else if(getType() == TYPE_CUSTOM){
			this.currentPercent = (Math.min(getCustomPercentTo(),(getCurrentPercent() + getCustomPercentIncrement())));
		}
	}
	
	public int getCurrentPercent(){
		return this.currentPercent;
	}
	
	public boolean isLoop() {
		return this.loop;
	}
	
	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	
	public int getType() {
		return this.type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getCustomPercentFrom() {
		return this.customPercentFrom;
	}
	
	public void setCustomPercentFrom(int customPercentFrom) {
		this.customPercentFrom = customPercentFrom;
	}
	
	public int getCustomPercentIncrement() {
		return this.customPercentIncrement;
	}
	
	public void setCustomPercentIncrement(int customPercentIncrement) {
		this.customPercentIncrement = customPercentIncrement;
	}
	
	public int getCustomPercentTo() {
		return this.customPercentTo;
	}
	
	public void setCustomPercentTo(int customPercentTo) {
		this.customPercentTo = customPercentTo;
	}
	
	public int getSinglePercent() {
		return this.singlePercent;
	}
	
	public void setSinglePercent(int singlePercent) {
		this.singlePercent = singlePercent;
	}
	
}
