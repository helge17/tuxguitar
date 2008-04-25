package org.herac.tuxguitar.io.ptb.base;

public class PTGuitarIn implements PTComponent{
	private int staff;
	private int trackInfo;
	
	public PTGuitarIn(int staff,int trackInfo){
		this.staff = staff;
		this.trackInfo = trackInfo;
	}
	
	public int getStaff() {
		return this.staff;
	}
	
	public int getTrackInfo() {
		return this.trackInfo;
	}
	
	public PTComponent getClone(){
		return new PTGuitarIn( getStaff(), getTrackInfo() );
	}
}