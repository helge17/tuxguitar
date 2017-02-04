package org.herac.tuxguitar.app.view.component.tab;

import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;

public class TablatureStyles extends TGLayoutStyles {
	
	public TablatureStyles(TGConfigManager config) {
		this.setBufferEnabled(true);
		this.setStringSpacing(config.getIntegerValue(TGConfigKeys.TAB_LINE_SPACING));
		this.setScoreLineSpacing(config.getIntegerValue(TGConfigKeys.SCORE_LINE_SPACING));
		this.setFirstMeasureSpacing(20);
		this.setMinBufferSeparator(20);
		this.setMinTopSpacing(30);
		this.setMinScoreTabSpacing(config.getIntegerValue(TGConfigKeys.MIN_SCORE_TABLATURE_SPACING));
		this.setFirstTrackSpacing(config.getIntegerValue(TGConfigKeys.FIRST_TRACK_SPACING));
		this.setTrackSpacing(config.getIntegerValue(TGConfigKeys.TRACK_SPACING));
		this.setChordFretIndexSpacing(8);
		this.setChordStringSpacing(5);
		this.setChordFretSpacing(6);
		this.setChordNoteSize(4);
		this.setChordLineWidth(1);
		this.setRepeatEndingSpacing(20);
		this.setTextSpacing(15);
		this.setMarkerSpacing(15);
		this.setLoopMarkerSpacing(5);
		this.setDivisionTypeSpacing(10);
		this.setEffectSpacing(8);
		
		this.setDefaultFont(config.getFontModelConfigValue(TGConfigKeys.FONT_DEFAULT));
		this.setNoteFont(config.getFontModelConfigValue(TGConfigKeys.FONT_NOTE));
		this.setTimeSignatureFont(config.getFontModelConfigValue(TGConfigKeys.FONT_TIME_SIGNATURE));
		this.setLyricFont(config.getFontModelConfigValue(TGConfigKeys.FONT_LYRIC));
		this.setTextFont(config.getFontModelConfigValue(TGConfigKeys.FONT_TEXT));
		this.setMarkerFont(config.getFontModelConfigValue(TGConfigKeys.FONT_MARKER));
		this.setGraceFont(config.getFontModelConfigValue(TGConfigKeys.FONT_GRACE));
		this.setChordFont(config.getFontModelConfigValue(TGConfigKeys.FONT_CHORD));
		this.setChordFretFont(config.getFontModelConfigValue(TGConfigKeys.FONT_CHORD_FRET));
		this.setBackgroundColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_BACKGROUND));
		this.setLineColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LINE));
		this.setScoreNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_SCORE_NOTE));
		this.setTabNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_TAB_NOTE));
		this.setPlayNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_PLAY_NOTE));
		this.setLoopSMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_S_MARKER));
		this.setLoopEMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_E_MARKER));
	}
}
