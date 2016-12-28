package org.herac.tuxguitar.app.printer;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

public class PrintController implements TGController {
	
	private static final TGColorModel DEFAULT_BACKGROUND_COLOR = new TGColorModel(0xff,0,0);
	private static final int DEFAULT_SCORE_LINE_SPACING = 7;
	private static final int DEFAULT_STRING_SPACING = 8;
	private static final int DEFAULT_HORIZONTAL_SPACING = 15;
	private static final int DEFAULT_MIN_SCORE_TAB_SPACING =  15;
	private static final int DEFAULT_TRACK_SPACING = 5;
	private static final int DEFAULT_FIRST_TRACK_SPACING = DEFAULT_TRACK_SPACING;
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
	
	private TGSong song;
	private TGSongManager songManager;
	private TGResourceFactory resourceFactory;
	private TGResourceBuffer resourceBuffer;
	
	public PrintController(TGSong song, TGSongManager songManager, TGResourceFactory resourceFactory){
		this.song = song;
		this.songManager = songManager;
		this.resourceFactory = resourceFactory;
		this.resourceBuffer = new TGResourceBuffer();
	}
	
	public TGSong getSong() {
		return song;
	}

	public TGSongManager getSongManager() {
		return this.songManager;
	}
	
	public TGResourceFactory getResourceFactory(){
		return this.resourceFactory;
	}
	
	public TGResourceBuffer getResourceBuffer() {
		return this.resourceBuffer;
	}

	public int getTrackSelection() {
		return -1;
	}
	
	public boolean isRunning(TGBeat beat) {
		return false;
	}
	
	public boolean isRunning(TGMeasure measure) {
		return false;
	}
	
	public boolean isLoopSHeader(TGMeasureHeader measureHeader) {
		return false;
	}
	
	public boolean isLoopEHeader(TGMeasureHeader measureHeader) {
		return false;
	}
	
	public void configureStyles(TGLayoutStyles styles){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		
		styles.setBufferEnabled(false);
		styles.setFirstMeasureSpacing(DEFAULT_HORIZONTAL_SPACING);
		styles.setMinBufferSeparator(DEFAULT_MIN_BUFFER_SEPARATOR);
		styles.setMinTopSpacing(DEFAULT_MIN_TOP_SPACING);
		styles.setMinScoreTabSpacing(DEFAULT_MIN_SCORE_TAB_SPACING);
		styles.setScoreLineSpacing(DEFAULT_SCORE_LINE_SPACING);
		styles.setFirstTrackSpacing(DEFAULT_FIRST_TRACK_SPACING);
		styles.setTrackSpacing(DEFAULT_TRACK_SPACING);
		styles.setStringSpacing(DEFAULT_STRING_SPACING);
		styles.setChordFretIndexSpacing(DEFAULT_CHORD_FRET_INDEX_SPACING);
		styles.setChordStringSpacing(DEFAULT_CHORD_STRING_SPACING);
		styles.setChordFretSpacing(DEFAULT_CHORD_FRET_SPACING);
		styles.setChordNoteSize(DEFAULT_CHORD_NOTE_SIZE);
		styles.setChordLineWidth(DEFAULT_CHORD_LINE_WIDTH);
		styles.setRepeatEndingSpacing(DEFAULT_REPEAT_ENDING_SPACING);
		styles.setTextSpacing(DEFAULT_TEXT_SPACING);
		styles.setMarkerSpacing(DEFAULT_MARKER_SPACING);
		styles.setDivisionTypeSpacing(DEFAULT_DIVISION_TYPE_SPACING);
		styles.setEffectSpacing(DEFAULT_EFFECT_SPACING);
		
		styles.setDefaultFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_DEFAULT));
		styles.setNoteFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_NOTE));
		styles.setTimeSignatureFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_TIME_SIGNATURE));
		styles.setLyricFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_LYRIC));
		styles.setTextFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_TEXT));
		styles.setGraceFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_GRACE));
		styles.setChordFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_CHORD));
		styles.setChordFretFont(config.getFontModelConfigValue(TGConfigKeys.FONT_CHORD_FRET));
		styles.setMarkerFont(config.getFontModelConfigValue(TGConfigKeys.FONT_MARKER));
		styles.setLineColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LINE));
		styles.setScoreNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_SCORE_NOTE));
		styles.setTabNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_TAB_NOTE));
		styles.setPlayNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_PLAY_NOTE));
		styles.setLoopSMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_S_MARKER));
		styles.setLoopEMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_E_MARKER));
		styles.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
	}
}
