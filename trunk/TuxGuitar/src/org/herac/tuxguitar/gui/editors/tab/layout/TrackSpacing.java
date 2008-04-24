package org.herac.tuxguitar.gui.editors.tab.layout;

public class TrackSpacing {
	public static final int SCORE = 0x01;
	public static final int TABLATURE = 0x02;
	
	/***     POSITIONS ARRAY INDICES     ***/
	public static final int POSITION_TOP = 0;
	public static final int POSITION_TEXT = 1;
	public static final int POSITION_BUFFER_SEPARATOR = 2;
	public static final int POSITION_REPEAT_ENDING = 3;
	public static final int POSITION_CHORD = 4;
	public static final int POSITION_SCORE_UP_LINES = 5;
	public static final int POSITION_SCORE_MIDDLE_LINES = 6;
	public static final int POSITION_SCORE_DOWN_LINES = 7;
	public static final int POSITION_TUPLETO = 8;
	public static final int POSITION_ACCENTUATED_EFFECT = 9;
	public static final int POSITION_HARMONIC_EFFEC = 10;
	public static final int POSITION_TAPPING_EFFEC = 11;
	public static final int POSITION_PALM_MUTE_EFFEC = 12;
	public static final int POSITION_VIBRATO_EFFEC = 13;
	public static final int POSITION_FADE_IN = 14;
	public static final int POSITION_TABLATURE_TOP_SEPARATOR = 15;
	public static final int POSITION_TABLATURE = 16;
	public static final int POSITION_LYRIC = 17;
	public static final int POSITION_BOTTOM = 18;
	
	private static final int[][] POSITIONS = new int[][]{
		/** SCORE **/
		new int[]{
				0,  //POSITION_TOP
				1,  //POSITION_TEXT
				2,  //POSITION_BUFFER_SEPARATOR
				3,  //POSITION_REPEAT_ENDING
				4,  //POSITION_CHORD
				11, //POSITION_SCORE_UP_LINES
				12, //POSITION_SCORE_MIDDLE_LINES
				13, //POSITION_SCORE_DOWN_LINES
				14, //POSITION_TUPLETO
				5,  //POSITION_ACCENTUATED_EFFECT
				6,  //POSITION_HARMONIC_EFFEC
				7,  //POSITION_TAPPING_EFFEC
				8,  //POSITION_PALM_MUTE_EFFEC
				9,  //POSITION_VIBRATO_EFFEC
				10, //POSITION_FADE_IN
				15, //POSITION_TABLATURE_TOP_SEPARATOR
				16, //POSITION_TABLATURE
				17, //POSITION_LYRIC
				18, //POSITION_BOTTOM
			},
			
		/** TABLATURE **/
		new int[]{
				0,  //POSITION_TOP
				1,  //POSITION_TEXT
				2,  //POSITION_BUFFER_SEPARATOR
				3,  //POSITION_REPEAT_ENDING
				4,  //POSITION_CHORD
				16, //POSITION_SCORE_UP_LINES
				17, //POSITION_SCORE_MIDDLE_LINES
				18, //POSITION_SCORE_DOWN_LINES
				13, //POSITION_TUPLETO
				5,  //POSITION_ACCENTUATED_EFFECT
				6,  //POSITION_HARMONIC_EFFEC
				7,  //POSITION_TAPPING_EFFEC
				8,  //POSITION_PALM_MUTE_EFFEC
				9,  //POSITION_VIBRATO_EFFEC
				10, //POSITION_FADE_IN
				11, //POSITION_TABLATURE_TOP_SEPARATOR
				12, //POSITION_TABLATURE
				14, //POSITION_LYRIC
				15, //POSITION_BOTTOM
			},
			
		/** SCORE | TABLATURE **/
		new int[]{
				0,  //POSITION_TOP
				1,  //POSITION_TEXT
				2,  //POSITION_BUFFER_SEPARATOR
				3,  //POSITION_REPEAT_ENDING
				4,  //POSITION_CHORD
				5,  //POSITION_SCORE_UP_LINES
				6,  //POSITION_SCORE_MIDDLE_LINES
				7,  //POSITION_SCORE_DOWN_LINES
				8,  //POSITION_TUPLETO
				9,  //POSITION_ACCENTUATED_EFFECT
				10, //POSITION_HARMONIC_EFFEC
				11, //POSITION_TAPPING_EFFEC
				12, //POSITION_PALM_MUTE_EFFEC
				13, //POSITION_VIBRATO_EFFEC
				14, //POSITION_FADE_IN
				15, //POSITION_TABLATURE_TOP_SEPARATOR
				16, //POSITION_TABLATURE
				17, //POSITION_LYRIC
				18, //POSITION_BOTTOM
			},
	};
	
	private int flags;
	private int[] spacing;
	
	public TrackSpacing(ViewLayout layout){
		this.flags = 0;
		this.flags |= ((layout.getStyle() & ViewLayout.DISPLAY_SCORE) != 0?SCORE:0);
		this.flags |= ((layout.getStyle() & ViewLayout.DISPLAY_TABLATURE) != 0 ?TABLATURE:0);
		this.spacing = new int[18];
	}
	
	public void setSize(int index,int size){
		this.spacing[ POSITIONS [this.flags -1] [index] ] = size;
	}
	
	public int getSize(){
		int spacing = 0;
		for(int i = 0;i < this.spacing.length; i ++){
			spacing += this.spacing[i];
		}
		return spacing;
	}
	
	public int getPosition(int index){
		int spacing = 0;
		for(int i = 0;i < POSITIONS[this.flags -1][index]; i ++){
			spacing += this.spacing[i];
		}
		return spacing;
	}
}