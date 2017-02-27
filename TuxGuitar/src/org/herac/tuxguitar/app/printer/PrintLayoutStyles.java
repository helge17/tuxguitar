package org.herac.tuxguitar.app.printer;

import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;

public class PrintLayoutStyles extends TGLayoutStyles {
	
	private static final TGColorModel DEFAULT_BACKGROUND_COLOR = new TGColorModel(0xff,0,0);
	private static final int DEFAULT_SCORE_LINE_SPACING = 7;
	private static final int DEFAULT_STRING_SPACING = 8;
	private static final int DEFAULT_HORIZONTAL_SPACING = 15;
	private static final int DEFAULT_MIN_SCORE_TAB_SPACING =  15;
	private static final int DEFAULT_TRACK_SPACING = 5;
	private static final int DEFAULT_FIRST_TRACK_SPACING = DEFAULT_TRACK_SPACING;
	private static final int DEFAULT_FIRST_NOTE_SPACING = 10;
	private static final int DEFAULT_CLEF_SPACING = 30;
	private static final int DEFAULT_MIN_BUFFER_SEPARATOR = 15;
	private static final int DEFAULT_MIN_TOP_SPACING = 20;
	private static final int DEFAULT_CHORD_FRET_INDEX_SPACING = 8;
	private static final int DEFAULT_CHORD_STRING_SPACING = 4;
	private static final int DEFAULT_CHORD_FRET_SPACING = 5;
	private static final int DEFAULT_CHORD_LINE_WIDTH = 1;
	private static final int DEFAULT_CHORD_NOTE_SIZE = 3;
	private static final int DEFAULT_EFFECT_SPACING = 8;
	private static final int DEFAULT_DIVISION_TYPE_SPACING = 10;
	private static final int DEFAULT_MARKER_SPACING = 15;
	private static final int DEFAULT_TEXT_SPACING = 15;
	private static final int DEFAULT_REPEAT_ENDING_SPACING = 20;
	private static final float[] DEFAULT_LINE_WIDTHS = new float[] {0, 1, 2, 3, 4, 5};
	private static final float[] DEFAULT_DURATION_WIDTHS = new float[] {25f, 22f, 20f, 18f, 16f};
	
	public PrintLayoutStyles(TGConfigManager config) {
		this.setBufferEnabled(false);
		this.setFirstMeasureSpacing(DEFAULT_HORIZONTAL_SPACING);
		this.setMinBufferSeparator(DEFAULT_MIN_BUFFER_SEPARATOR);
		this.setMinTopSpacing(DEFAULT_MIN_TOP_SPACING);
		this.setMinScoreTabSpacing(DEFAULT_MIN_SCORE_TAB_SPACING);
		this.setScoreLineSpacing(DEFAULT_SCORE_LINE_SPACING);
		this.setFirstTrackSpacing(DEFAULT_FIRST_TRACK_SPACING);
		this.setTrackSpacing(DEFAULT_TRACK_SPACING);
		this.setStringSpacing(DEFAULT_STRING_SPACING);
		this.setFirstNoteSpacing(DEFAULT_FIRST_NOTE_SPACING);
		this.setMeasureLeftSpacing(DEFAULT_HORIZONTAL_SPACING);
		this.setMeasureRightSpacing(DEFAULT_HORIZONTAL_SPACING);
		this.setClefSpacing(DEFAULT_CLEF_SPACING);
		this.setKeySignatureSpacing(DEFAULT_HORIZONTAL_SPACING);
		this.setTimeSignatureSpacing(DEFAULT_HORIZONTAL_SPACING);
		this.setChordFretIndexSpacing(DEFAULT_CHORD_FRET_INDEX_SPACING);
		this.setChordStringSpacing(DEFAULT_CHORD_STRING_SPACING);
		this.setChordFretSpacing(DEFAULT_CHORD_FRET_SPACING);
		this.setChordNoteSize(DEFAULT_CHORD_NOTE_SIZE);
		this.setChordLineWidth(DEFAULT_CHORD_LINE_WIDTH);
		this.setRepeatEndingSpacing(DEFAULT_REPEAT_ENDING_SPACING);
		this.setTextSpacing(DEFAULT_TEXT_SPACING);
		this.setMarkerSpacing(DEFAULT_MARKER_SPACING);
		this.setDivisionTypeSpacing(DEFAULT_DIVISION_TYPE_SPACING);
		this.setEffectSpacing(DEFAULT_EFFECT_SPACING);
		this.setLineWidths(DEFAULT_LINE_WIDTHS);
		this.setDurationWidths(DEFAULT_DURATION_WIDTHS);
		
		this.setDefaultFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_DEFAULT));
		this.setNoteFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_NOTE));
		this.setTimeSignatureFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_TIME_SIGNATURE));
		this.setLyricFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_LYRIC));
		this.setTextFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_TEXT));
		this.setGraceFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_GRACE));
		this.setChordFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_CHORD));
		this.setChordFretFont(config.getFontModelConfigValue(TGConfigKeys.FONT_CHORD_FRET));
		this.setMarkerFont(config.getFontModelConfigValue(TGConfigKeys.FONT_MARKER));
		this.setLineColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LINE));
		this.setScoreNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_SCORE_NOTE));
		this.setTabNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_TAB_NOTE));
		this.setPlayNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_PLAY_NOTE));
		this.setLoopSMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_S_MARKER));
		this.setLoopEMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_E_MARKER));
		this.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
	}
}
