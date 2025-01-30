package org.herac.tuxguitar.graphics.control;


public class TGBeatSpacing extends TGSpacing {
	
	/***     POSITIONS ARRAY INDICES     ***/
	public static final int POSITION_ACCENTUATED_EFFECT = 0;
	public static final int POSITION_HEAVY_ACCENTUATED_EFFECT = 1;
	public static final int POSITION_HARMONIC_EFFECT = 2;
	public static final int POSITION_TAPPING_EFFECT = 3;
	public static final int POSITION_SLAPPING_EFFECT = 4;
	public static final int POSITION_POPPING_EFFECT = 5;
	public static final int POSITION_PALM_MUTE_EFFECT = 6;
	public static final int POSITION_LET_RING_EFFECT = 7;
	public static final int POSITION_VIBRATO_EFFECT = 8;
	public static final int POSITION_TRILL_EFFECT = 9;
	public static final int POSITION_FADE_IN_EFFECT = 10;
	public static final int POSITION_BEND_VALUE = 11;
	
	private static final int[] EFFECT_POSITIONS = new int[]{
		POSITION_ACCENTUATED_EFFECT,
		POSITION_HEAVY_ACCENTUATED_EFFECT,
		POSITION_HARMONIC_EFFECT,
		POSITION_TAPPING_EFFECT,
		POSITION_SLAPPING_EFFECT,
		POSITION_POPPING_EFFECT,
		POSITION_PALM_MUTE_EFFECT,
		POSITION_LET_RING_EFFECT,
		POSITION_VIBRATO_EFFECT,
		POSITION_TRILL_EFFECT,
		POSITION_FADE_IN_EFFECT,
		POSITION_BEND_VALUE,
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
