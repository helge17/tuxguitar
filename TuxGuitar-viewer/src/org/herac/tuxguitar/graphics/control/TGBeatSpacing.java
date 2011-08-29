package org.herac.tuxguitar.graphics.control;


public class TGBeatSpacing extends TGSpacing {
	
	/***     POSITIONS ARRAY INDICES     ***/
	public static final int POSITION_ACCENTUATED_EFFECT = 0;
	public static final int POSITION_HEAVY_ACCENTUATED_EFFECT = 1;
	public static final int POSITION_HARMONIC_EFFEC = 2;
	public static final int POSITION_TAPPING_EFFEC = 3;
	public static final int POSITION_SLAPPING_EFFEC = 4;
	public static final int POSITION_POPPING_EFFEC = 5;
	public static final int POSITION_PALM_MUTE_EFFEC = 6;
	public static final int POSITION_LET_RING_EFFEC = 7;
	public static final int POSITION_VIBRATO_EFFEC = 8;
	public static final int POSITION_TRILL_EFFEC = 9;
	public static final int POSITION_FADE_IN = 10;
	
	private static final int[] EFFECT_POSITIONS = new int[]{
		POSITION_ACCENTUATED_EFFECT,
		POSITION_HEAVY_ACCENTUATED_EFFECT,
		POSITION_HARMONIC_EFFEC,
		POSITION_TAPPING_EFFEC,
		POSITION_SLAPPING_EFFEC,
		POSITION_POPPING_EFFEC,
		POSITION_PALM_MUTE_EFFEC,
		POSITION_LET_RING_EFFEC,
		POSITION_VIBRATO_EFFEC,
		POSITION_TRILL_EFFEC,
		POSITION_FADE_IN,
	};
	
	private static final int[][] POSITIONS = new int[][]{
		/** SCORE **/
		EFFECT_POSITIONS ,
		/** TABLATURE **/
		EFFECT_POSITIONS ,
		/** SCORE | TABLATURE **/
		EFFECT_POSITIONS ,
	};
	
	public TGBeatSpacing(TGLayout layout) {
		super(layout, POSITIONS, EFFECT_POSITIONS.length );
	}
}
