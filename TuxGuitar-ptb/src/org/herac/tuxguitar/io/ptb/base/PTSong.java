package org.herac.tuxguitar.io.ptb.base;


public class PTSong {
	private PTSongInfo info;
	private PTTrack track1;
	private PTTrack track2;
	
	public PTSong(){
		this.info = new PTSongInfo();
		this.track1 = new PTTrack();
		this.track2 = new PTTrack();
	}
	
	public PTTrack getTrack1() {
		return this.track1;
	}
	
	public PTTrack getTrack2() {
		return this.track2;
	}
	
	public PTSongInfo getInfo(){
		return this.info;
	}
}
