/*
 * Created on 27-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.system.config;

import java.awt.Color;
import java.awt.Font;

import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGConfig{
	
	private static final String DEFAULT_FONT_NAME = "Arial";
	
	public static final String LANGUAGE = "";
	
	public static final int LAYOUT_STYLE = (ViewLayout.DISPLAY_TABLATURE | ViewLayout.DISPLAY_COMPACT | ViewLayout.DISPLAY_CHORD_NAME);
	public static final int MIN_SCORE_TABLATURE_SPACING = 20;
	public static final int SCORE_LINE_SPACING = 8;
	public static final int TAB_LINE_SPACING = 10;
	public static final int FIRST_TRACK_SPACING = 0;//20;
	public static final int TRACK_SPACING = 10;
	
	public static final Font FONT_WIDGETS = new Font(DEFAULT_FONT_NAME, Font.BOLD ,12);
	
	public static final Font FONT_DEFAULT = new Font(DEFAULT_FONT_NAME, Font.PLAIN ,9);
	public static final Font FONT_NOTE = new Font(DEFAULT_FONT_NAME, Font.PLAIN ,9);
	public static final Font FONT_TIME_SIGNATURE = new Font(DEFAULT_FONT_NAME, Font.BOLD ,14);
	public static final Font FONT_LYRIC = new Font(DEFAULT_FONT_NAME, Font.ITALIC ,7);
	public static final Font FONT_TEXT = new Font(DEFAULT_FONT_NAME, Font.PLAIN ,9);
	public static final Font FONT_MARKER = new Font(DEFAULT_FONT_NAME, Font.PLAIN ,9);
	public static final Font FONT_CHORD = new Font(DEFAULT_FONT_NAME, Font.PLAIN ,9);
	public static final Font FONT_CHORD_FRET = new Font(DEFAULT_FONT_NAME, Font.PLAIN ,7);
	public static final Font FONT_GRACE = new Font(DEFAULT_FONT_NAME, Font.PLAIN ,6);
	
	public static final Font FONT_LOADING_MESSAGE = new Font(DEFAULT_FONT_NAME, Font.BOLD ,18);
	public static final Color COLOR_LOADING_MESSAGE = new Color(200,200,200);
	
	public static final Color COLOR_BACKGROUND = new Color(255,255,255);
	public static final Color COLOR_LINE = new Color(225,225,225);
	public static final Color COLOR_SCORE_NOTE = new Color(0,0,0);
	public static final Color COLOR_TAB_NOTE = new Color(0,0,0);
	public static final Color COLOR_PLAY_NOTE = new Color(255,0,0);
	
	public static String LOOK_FEEL = null;
	public static String SONG_URL = null;
	public static String SOUNDBANK_URL = null;
	
	public static String MIDI_PORT = null;
	public static String MIDI_SEQUENCER = "tuxguitar.sequencer";
}
