package org.herac.tuxguitar.song.models;

public interface TGVelocities {
	
	public static final int MIN_VELOCITY = 15;
	
	public static final int VELOCITY_INCREMENT = 16;
	
	public static final int PIANO_PIANISSIMO = (MIN_VELOCITY);
	
	public static final int PIANISSIMO = (MIN_VELOCITY + VELOCITY_INCREMENT);
	
	public static final int PIANO = (MIN_VELOCITY + (VELOCITY_INCREMENT * 2));
	
	public static final int MEZZO_PIANO = (MIN_VELOCITY + (VELOCITY_INCREMENT * 3));
	
	public static final int MEZZO_FORTE = (MIN_VELOCITY + (VELOCITY_INCREMENT * 4));
	
	public static final int FORTE = (MIN_VELOCITY + (VELOCITY_INCREMENT * 5));
	
	public static final int FORTISSIMO = (MIN_VELOCITY + (VELOCITY_INCREMENT * 6));
	
	public static final int FORTE_FORTISSIMO = (MIN_VELOCITY + (VELOCITY_INCREMENT * 7));
	
	public static final int DEFAULT = FORTE;
	
}
