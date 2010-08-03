package org.herac.tuxguitar.app.editors;

public interface TGRedrawListener {
	
	public static final int NORMAL = 1;
	public static final int PLAYING_THREAD = 2;
	public static final int PLAYING_NEW_BEAT = 3;
	
	public void doRedraw( int type );
}
