package org.herac.tuxguitar.app.system.config;

import java.awt.Color;
import java.awt.Font;

import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFontModel;

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
	
	public static final UIFontModel FONT_DEFAULT = new UIFontModel(DEFAULT_FONT_NAME, 9, false, false);
	public static final UIFontModel FONT_NOTE = new UIFontModel(DEFAULT_FONT_NAME, 9, false, false);
	public static final UIFontModel FONT_LYRIC = new UIFontModel(DEFAULT_FONT_NAME, 7, false, true);
	public static final UIFontModel FONT_TEXT = new UIFontModel(DEFAULT_FONT_NAME, 9, false, false);
	public static final UIFontModel FONT_MARKER = new UIFontModel(DEFAULT_FONT_NAME, 9, false, false);
	public static final UIFontModel FONT_CHORD = new UIFontModel(DEFAULT_FONT_NAME, 9, false, false);
	public static final UIFontModel FONT_CHORD_FRET = new UIFontModel(DEFAULT_FONT_NAME, 7, false, false);
	public static final UIFontModel FONT_GRACE = new UIFontModel(DEFAULT_FONT_NAME, 6, false, false);
	
	public static final UIColorModel COLOR_LOADING_MESSAGE = new UIColorModel(200,200,200);
	public static final UIColorModel COLOR_FOREGROUND = new UIColorModel(0,0,0);
	public static final UIColorModel COLOR_BACKGROUND = new UIColorModel(255,255,255);
	public static final UIColorModel COLOR_LINE = new UIColorModel(225,225,225);
	public static final UIColorModel COLOR_SCORE_NOTE = new UIColorModel(0,0,0);
	public static final UIColorModel COLOR_TAB_NOTE = new UIColorModel(0,0,0);
	public static final UIColorModel COLOR_PLAY_NOTE = new UIColorModel(255,0,0);
	public static final UIColorModel COLOR_LOOP_S_MARKER = new UIColorModel(0,255,0);
	public static final UIColorModel COLOR_LOOP_E_MARKER = new UIColorModel(255,0,0);
	public static final UIColorModel COLOR_MEASURE_NUMBER = new UIColorModel(255,0,0);
	
	public static String LOOK_FEEL = null;
	public static String SONG_URL = null;
	
	public static String MIDI_PORT = null;
	public static String MIDI_SEQUENCER = "tuxguitar.sequencer";
}
