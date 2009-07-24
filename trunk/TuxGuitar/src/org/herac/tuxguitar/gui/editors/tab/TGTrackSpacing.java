package org.herac.tuxguitar.gui.editors.tab;

import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;

public class TGTrackSpacing extends TGSpacing {
	
	/***     POSITIONS ARRAY INDICES     ***/
	public static final int POSITION_TOP = 0;
	public static final int POSITION_LOOP_MARKER = 1;
	public static final int POSITION_MARKER = 2;
	public static final int POSITION_TEXT = 3;
	public static final int POSITION_BUFFER_SEPARATOR = 4;
	public static final int POSITION_REPEAT_ENDING = 5;
	public static final int POSITION_CHORD = 6;
	public static final int POSITION_SCORE_UP_LINES = 7;
	public static final int POSITION_SCORE_MIDDLE_LINES = 8;
	public static final int POSITION_SCORE_DOWN_LINES = 9;
	public static final int POSITION_DIVISION_TYPE = 10;
	public static final int POSITION_EFFECTS = 11;
	public static final int POSITION_TABLATURE_TOP_SEPARATOR = 12;
	public static final int POSITION_TABLATURE = 13;
	public static final int POSITION_LYRIC = 14;
	public static final int POSITION_BOTTOM = 15;
	
	private static final int[][] POSITIONS = new int[][]{
		/** SCORE **/
		new int[]{
				0,  //POSITION_TOP
				1,  //POSITION_LOOP_MARKER
				2,  //POSITION_MARKER
				3,  //POSITION_TEXT
				4,  //POSITION_BUFFER_SEPARATOR
				5,  //POSITION_REPEAT_ENDING
				6,  //POSITION_CHORD
				8,  //POSITION_SCORE_UP_LINES
				9,  //POSITION_SCORE_MIDDLE_LINES
				10, //POSITION_SCORE_DOWN_LINES
				11, //POSITION_DIVISION_TYPE
				7,  //POSITION_EFFECTS
				12, //POSITION_TABLATURE_TOP_SEPARATOR
				13, //POSITION_TABLATURE
				14, //POSITION_LYRIC
				15, //POSITION_BOTTOM
			},
			
		/** TABLATURE **/
		new int[]{
				0,  //POSITION_TOP
				1,  //POSITION_LOOP_MARKER
				2,  //POSITION_MARKER
				3,  //POSITION_TEXT
				4,  //POSITION_BUFFER_SEPARATOR
				5,  //POSITION_REPEAT_ENDING
				6,  //POSITION_CHORD
				13, //POSITION_SCORE_UP_LINES
				14, //POSITION_SCORE_MIDDLE_LINES
				15, //POSITION_SCORE_DOWN_LINES
				10, //POSITION_DIVISION_TYPE
				7,  //POSITION_EFFECTS
				8,  //POSITION_TABLATURE_TOP_SEPARATOR
				9,  //POSITION_TABLATURE
				11, //POSITION_LYRIC
				12, //POSITION_BOTTOM
			},
			
		/** SCORE | TABLATURE **/
		new int[]{
				0,  //POSITION_TOP
				1,  //POSITION_LOOP_MARKER
				2,  //POSITION_MARKER
				3,  //POSITION_TEXT
				4,  //POSITION_BUFFER_SEPARATOR
				5,  //POSITION_REPEAT_ENDING
				6,  //POSITION_CHORD
				7,  //POSITION_SCORE_UP_LINES
				8,  //POSITION_SCORE_MIDDLE_LINES
				9,  //POSITION_SCORE_DOWN_LINES
				10, //POSITION_DIVISION_TYPE
				11, //POSITION_EFFECTS
				12, //POSITION_TABLATURE_TOP_SEPARATOR
				13, //POSITION_TABLATURE
				14, //POSITION_LYRIC
				15, //POSITION_BOTTOM
			},
	};
	
	public TGTrackSpacing(ViewLayout layout){
		super( layout , POSITIONS , 16);
	}
}