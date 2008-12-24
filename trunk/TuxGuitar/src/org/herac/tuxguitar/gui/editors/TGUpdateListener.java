package org.herac.tuxguitar.gui.editors;

public interface TGUpdateListener {
	public static final int SELECTION = 1;
	public static final int SONG_UPDATED = 2;
	public static final int SONG_LOADED = 3;
	
	public void doUpdate( int type );
}
