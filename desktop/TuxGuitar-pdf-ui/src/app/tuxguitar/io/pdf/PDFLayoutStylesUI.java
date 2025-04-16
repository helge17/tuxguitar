package app.tuxguitar.io.pdf;

import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.util.TGContext;

public class PDFLayoutStylesUI extends PDFLayoutStyles {

	public PDFLayoutStylesUI(TGConfigManager config, TGContext context) {
		super(context);
		this.setLineColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LINE));
		this.setLineColorInvalid(config.getColorModelConfigValue(TGConfigKeys.COLOR_LINE));
		this.setScoreNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_SCORE_NOTE));
		this.setTabNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_TAB_NOTE));
		this.setPlayNoteColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_PLAY_NOTE));
		this.setLoopSMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_S_MARKER));
		this.setLoopEMarkerColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_LOOP_E_MARKER));
		this.setMeasureNumberColor(config.getColorModelConfigValue(TGConfigKeys.COLOR_MEASURE_NUMBER));
	}
}
