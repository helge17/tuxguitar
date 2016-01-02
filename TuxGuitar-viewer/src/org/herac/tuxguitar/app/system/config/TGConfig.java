package org.herac.tuxguitar.app.system.config;

import java.awt.Color;
import java.awt.Font;

import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.control.TGLayout;

public class TGConfig{
	
	private static final String DEFAULT_FONT_NAME = "Arial";
	
	public static final String LANGUAGE = "";
	
	public static final int LAYOUT_STYLE = (TGLayout.DISPLAY_TABLATURE | TGLayout.DISPLAY_COMPACT | TGLayout.DISPLAY_CHORD_NAME);
	public static final int MIN_SCORE_TABLATURE_SPACING = 20;
	public static final int SCORE_LINE_SPACING = 8;
	public static final int TAB_LINE_SPACING = 10;
	public static final int FIRST_TRACK_SPACING = 0;//20;
	public static final int TRACK_SPACING = 10;
	
	public static final Font FONT_WIDGETS = new Font(DEFAULT_FONT_NAME, Font.BOLD, 12);
	public static final Font FONT_LOADING_MESSAGE = new Font(DEFAULT_FONT_NAME, Font.BOLD ,18);
	public static final Color COLOR_WIDGET_BACKGROUND = new Color(255,255,255);
	
	public static final TGFontModel FONT_DEFAULT = new TGFontModel(DEFAULT_FONT_NAME, 9, false, false);
	public static final TGFontModel FONT_NOTE = new TGFontModel(DEFAULT_FONT_NAME, 9, false, false);
	public static final TGFontModel FONT_TIME_SIGNATURE = new TGFontModel(DEFAULT_FONT_NAME, 14, true, false);
	public static final TGFontModel FONT_LYRIC = new TGFontModel(DEFAULT_FONT_NAME, 7, false, true);
	public static final TGFontModel FONT_TEXT = new TGFontModel(DEFAULT_FONT_NAME, 9, false, false);
	public static final TGFontModel FONT_MARKER = new TGFontModel(DEFAULT_FONT_NAME, 9, false, false);
	public static final TGFontModel FONT_CHORD = new TGFontModel(DEFAULT_FONT_NAME, 9, false, false);
	public static final TGFontModel FONT_CHORD_FRET = new TGFontModel(DEFAULT_FONT_NAME, 7, false, false);
	public static final TGFontModel FONT_GRACE = new TGFontModel(DEFAULT_FONT_NAME, 6, false, false);
	
	public static final TGColorModel COLOR_LOADING_MESSAGE = new TGColorModel(200,200,200);
	public static final TGColorModel COLOR_BACKGROUND = new TGColorModel(255,255,255);
	public static final TGColorModel COLOR_LINE = new TGColorModel(225,225,225);
	public static final TGColorModel COLOR_SCORE_NOTE = new TGColorModel(0,0,0);
	public static final TGColorModel COLOR_TAB_NOTE = new TGColorModel(0,0,0);
	public static final TGColorModel COLOR_PLAY_NOTE = new TGColorModel(255,0,0);
	public static final TGColorModel COLOR_LOOP_S_MARKER = new TGColorModel(0,255,0);
	public static final TGColorModel COLOR_LOOP_E_MARKER = new TGColorModel(255,0,0);
	
	public static String LOOK_FEEL = null;
	public static String SONG_URL = null;
	
	public static String MIDI_PORT = null;
	public static String MIDI_SEQUENCER = "tuxguitar.sequencer";
}
