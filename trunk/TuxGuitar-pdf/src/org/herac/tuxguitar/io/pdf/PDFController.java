package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

import com.itextpdf.text.FontFactory;

public class PDFController implements TGController {
	
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
	
	private TGSong song;
	private TGSongManager songManager;
	private TGResourceFactory resourceFactory;
	private TGResourceBuffer resourceBuffer;
	
	public PDFController(TGSong song, TGSongManager songManager, TGResourceFactory resourceFactory){
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
	
	public void configureStyles(TGLayoutStyles styles) {
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		
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
		styles.setChordLineWidth(1);
		styles.setRepeatEndingSpacing(20);
		styles.setTextSpacing(15);
		styles.setMarkerSpacing(15);
		styles.setDivisionTypeSpacing(10);
		styles.setEffectSpacing(8);
		styles.setDefaultFont(new TGFontModel(FontFactory.TIMES_ROMAN, 8, false, false));
		styles.setNoteFont(new TGFontModel(FontFactory.TIMES_BOLD, 9, true, false));
		styles.setTimeSignatureFont(new TGFontModel(FontFactory.TIMES_BOLD, 15, true, false));
		styles.setLyricFont(new TGFontModel(FontFactory.TIMES_ROMAN, 8, false, false));
		styles.setTextFont(new TGFontModel(FontFactory.TIMES_ROMAN, 8, false, false));
		styles.setGraceFont(new TGFontModel(FontFactory.TIMES_ROMAN, 8, false, false));
		styles.setChordFont(new TGFontModel(FontFactory.TIMES_ROMAN, 8, false, false));
		styles.setChordFretFont(new TGFontModel(FontFactory.TIMES_ROMAN, 8, false, false));
		styles.setMarkerFont(new TGFontModel(FontFactory.TIMES_ROMAN, 8, false, false));
		styles.setBackgroundColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_BACKGROUND));
		styles.setLineColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LINE));
		styles.setScoreNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_SCORE_NOTE));
		styles.setTabNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_TAB_NOTE));
		styles.setPlayNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_PLAY_NOTE));
		styles.setLoopSMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_S_MARKER));
		styles.setLoopEMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_E_MARKER));
	}
}
