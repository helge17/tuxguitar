package org.herac.tuxguitar.app.printer;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

public class PrintController implements TGController {
	
	private static final int DEFAULT_SCORE_LINE_SPACING = 7;
	private static final int DEFAULT_STRING_SPACING = 8;
	private static final int DEFAULT_HORIZONTAL_SPACING = 15;
	private static final int MIN_SCORE_TAB_SPACING =  15;
	private static final int DEFAULT_TRACK_SPACING = 5;
	private static final int DEFAULT_FIRST_TRACK_SPACING = DEFAULT_TRACK_SPACING;
	private static final int DEFAULT_MIN_BUFFER_SEPARATOR = 15;
	private static final int DEFAULT_MIN_TOP_SPACING = 20;
	private static final int CHORD_FRET_INDEX_SPACING = 8;
	private static final int CHORD_STRING_SPACING = 4;
	private static final int CHORD_FRET_SPACING = 5;
	
	private TGSongManager songManager;
	private TGResourceFactory resourceFactory;
	
	public PrintController(TGSongManager songManager, TGResourceFactory resourceFactory){
		this.songManager = songManager;
		this.resourceFactory = resourceFactory;
	}
	
	public TGSongManager getSongManager() {
		return this.songManager;
	}
	
	public TGResourceFactory getResourceFactory(){
		return this.resourceFactory;
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
		TGConfigManager config = TuxGuitar.instance().getConfig();
		
		styles.setBufferEnabled(false);
		styles.setFirstMeasureSpacing(DEFAULT_HORIZONTAL_SPACING);
		styles.setMinBufferSeparator(DEFAULT_MIN_BUFFER_SEPARATOR);
		styles.setMinTopSpacing(DEFAULT_MIN_TOP_SPACING);
		styles.setMinScoreTabSpacing(MIN_SCORE_TAB_SPACING);
		styles.setScoreLineSpacing(DEFAULT_SCORE_LINE_SPACING);
		styles.setFirstTrackSpacing(DEFAULT_FIRST_TRACK_SPACING);
		styles.setTrackSpacing(DEFAULT_TRACK_SPACING);
		styles.setStringSpacing(DEFAULT_STRING_SPACING);
		styles.setChordFretIndexSpacing(CHORD_FRET_INDEX_SPACING);
		styles.setChordStringSpacing(CHORD_STRING_SPACING);
		styles.setChordFretSpacing(CHORD_FRET_SPACING);
		styles.setChordNoteSize(3);
		styles.setRepeatEndingSpacing(20);
		styles.setTextSpacing(15);
		styles.setMarkerSpacing(15);
		styles.setDivisionTypeSpacing(10);
		styles.setEffectSpacing(8);
		
		styles.setDefaultFont(config.getFontModelConfigValue(TGConfigKeys.FONT_DEFAULT));
		styles.setNoteFont(config.getFontModelConfigValue(TGConfigKeys.FONT_NOTE));
		styles.setTimeSignatureFont(config.getFontModelConfigValue(TGConfigKeys.FONT_TIME_SIGNATURE));
		styles.setLyricFont(config.getFontModelConfigValue(TGConfigKeys.FONT_LYRIC));
		styles.setTextFont(config.getFontModelConfigValue(TGConfigKeys.FONT_TEXT));
		styles.setMarkerFont(config.getFontModelConfigValue(TGConfigKeys.FONT_MARKER));
		styles.setGraceFont(config.getFontModelConfigValue(TGConfigKeys.FONT_GRACE));
		styles.setChordFont(config.getFontModelConfigValue(TGConfigKeys.FONT_CHORD));
		styles.setChordFretFont(config.getFontModelConfigValue(TGConfigKeys.FONT_CHORD_FRET));
		styles.setPrinterDefaultFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_DEFAULT));
		styles.setPrinterNoteFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_NOTE));
		styles.setPrinterTimeSignatureFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_TIME_SIGNATURE));
		styles.setPrinterLyricFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_LYRIC));
		styles.setPrinterTextFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_TEXT));
		styles.setPrinterGraceFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_GRACE));
		styles.setPrinterChordFont(config.getFontModelConfigValue(TGConfigKeys.FONT_PRINTER_CHORD));
		styles.setBackgroundColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_BACKGROUND));
		styles.setLineColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LINE));
		styles.setScoreNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_SCORE_NOTE));
		styles.setTabNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_TAB_NOTE));
		styles.setPlayNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_PLAY_NOTE));
		styles.setLoopSMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_S_MARKER));
		styles.setLoopEMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_E_MARKER));
		
	}
}
