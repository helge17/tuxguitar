package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;

public class PDFLayoutStylesUI extends PDFLayoutStyles {
	
	public PDFLayoutStylesUI(TGConfigManager config) {
		this.setLineColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LINE));
		this.setScoreNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_SCORE_NOTE));
		this.setTabNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_TAB_NOTE));
		this.setPlayNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_PLAY_NOTE));
		this.setLoopSMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_S_MARKER));
		this.setLoopEMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_E_MARKER));
	}
}
