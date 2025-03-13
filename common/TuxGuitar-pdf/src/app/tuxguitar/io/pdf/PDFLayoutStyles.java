package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.util.TGContext;

import com.itextpdf.text.pdf.BaseFont;

public class PDFLayoutStyles extends TGLayoutStyles {

	public PDFLayoutStyles(TGContext context) {
		PDFSettingsManager settingsMgr = PDFSettingsManager.getInstance(context);

		this.setBufferEnabled(false);
		this.setFirstMeasureSpacing(settingsMgr.getSetting(PDFSettings.FIRST_MEASURE_SPACING));
		this.setMinBufferSeparator(settingsMgr.getSetting(PDFSettings.MIN_BUFFER_SEPARATOR));
		this.setMinTopSpacing(settingsMgr.getSetting(PDFSettings.TOP_SPACING));
		this.setMinScoreTabSpacing(settingsMgr.getSetting(PDFSettings.MIN_SCORE_TAB_SPACING));
		this.setScoreLineSpacing(settingsMgr.getSetting(PDFSettings.SCORE_LINE_SPACING));
		this.setFirstTrackSpacing(settingsMgr.getSetting(PDFSettings.FIRST_TRACK_SPACING));
		this.setTrackSpacing(settingsMgr.getSetting(PDFSettings.TRACK_SPACING));
		this.setStringSpacing(settingsMgr.getSetting(PDFSettings.STRING_SPACING));
		this.setChordFretIndexSpacing(settingsMgr.getSetting(PDFSettings.CHORD_FRET_INDEX_SPACING));
		this.setChordStringSpacing(settingsMgr.getSetting(PDFSettings.CHORD_STRING_SPACING));
		this.setChordFretSpacing(settingsMgr.getSetting(PDFSettings.CHORD_FRET_SPACING));
		this.setChordNoteSize(settingsMgr.getSetting(PDFSettings.CHORD_NOTE_SIZE));
		this.setChordLineWidth(settingsMgr.getSetting(PDFSettings.CHORD_LINE_WIDTH));
		this.setRepeatEndingSpacing(settingsMgr.getSetting(PDFSettings.REPEAT_ENDING_SPACING));
		this.setTextSpacing(settingsMgr.getSetting(PDFSettings.TEXT_SPACING));
		this.setMarkerSpacing(settingsMgr.getSetting(PDFSettings.MARKER_SPACING));
		this.setDivisionTypeSpacing(settingsMgr.getSetting(PDFSettings.DIVISION_TYPE_SPACING));
		this.setEffectSpacing(settingsMgr.getSetting(PDFSettings.EFFECT_SPACING));
		this.setFirstNoteSpacing(settingsMgr.getSetting(PDFSettings.FIRST_NOTE_SPACING));
		this.setMeasureLeftSpacing(settingsMgr.getSetting(PDFSettings.MEASURE_LEFT_SPACING));
		this.setMeasureRightSpacing(settingsMgr.getSetting(PDFSettings.MEASURE_RIGHT_SPACING));
		this.setClefSpacing(settingsMgr.getSetting(PDFSettings.CLEF_SPACING));
		this.setKeySignatureSpacing(settingsMgr.getSetting(PDFSettings.KEY_SIGNATURE_SPACING));
		this.setTimeSignatureSpacing(settingsMgr.getSetting(PDFSettings.TIME_SIGNATURE_SPACING));

		this.setLineWidths(new float[] {0, 1, 2, 3, 4, 5});
		this.setDurationWidths(new float[] {18f, 18f, 16f, 15f, 14f});
		this.setDefaultFont(new UIFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setNoteFont(new UIFontModel(BaseFont.TIMES_BOLD, 9, true, false));
		this.setLyricFont(new UIFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setTextFont(new UIFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setGraceFont(new UIFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setChordFont(new UIFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setChordFretFont(new UIFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setMarkerFont(new UIFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setForegroundColor(new UIColorModel(0x00,0x00,0x00));
		this.setBackgroundColor(new UIColorModel(0xff,0xff,0xff));
		this.setLineColor(new UIColorModel(0x00,0x00,0x00));
		this.setScoreNoteColor(new UIColorModel(0x00,0x00,0x00));
		this.setTabNoteColor(new UIColorModel(0x00,0x00,0x00));
		this.setPlayNoteColor(new UIColorModel(0x00,0x00,0x00));
		this.setLoopSMarkerColor(new UIColorModel(0x00,0x00,0x00));
		this.setLoopEMarkerColor(new UIColorModel(0x00,0x00,0x00));
		this.setMeasureNumberColor(new UIColorModel(0xff,0x00,0x00));
	}
}
