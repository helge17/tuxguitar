package org.herac.tuxguitar.io.pdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFSettings {

	private Map<String, Float> settingsMap;
	// ordered list, to define display order in dialog (keep similar settings close to each other)
	private List<String> orderedKeys;

	// keys
	public static final String FIRST_MEASURE_SPACING = "pdf.first-measure-spacing";
	public static final String MIN_BUFFER_SEPARATOR = "pdf.min-buffer-separator";
	public static final String TOP_SPACING = "pdf.top-spacing";
	public static final String MIN_SCORE_TAB_SPACING = "pdf.min-score-tab-spacing";
	public static final String SCORE_LINE_SPACING = "pdf.score-line-spacing";
	public static final String FIRST_TRACK_SPACING = "pdf.first-track-spacing";
	public static final String TRACK_SPACING = "pdf.track-spacing";
	public static final String STRING_SPACING = "pdf.string-spacing";
	public static final String CHORD_FRET_INDEX_SPACING = "pdf.chord-fret-index-spacing";
	public static final String CHORD_STRING_SPACING = "pdf.chord-string-spacing";
	public static final String CHORD_FRET_SPACING = "pdf.chord-fret-spacing";
	public static final String CHORD_NOTE_SIZE = "pdf.chord-note-size";
	public static final String CHORD_LINE_WIDTH = "pdf.chord-line-width";
	public static final String REPEAT_ENDING_SPACING = "pdf.repeat-ending-spacing";
	public static final String TEXT_SPACING = "pdf.text-spacing";
	public static final String MARKER_SPACING = "pdf.marker-spacing";
	public static final String DIVISION_TYPE_SPACING = "pdf.division-type-spacing";
	public static final String EFFECT_SPACING = "pdf.effect-spacing";
	public static final String FIRST_NOTE_SPACING = "pdf.first-note-spacing";
	public static final String MEASURE_LEFT_SPACING = "pdf.measure-left-spacing";
	public static final String MEASURE_RIGHT_SPACING = "pdf.measure-right-spacing";
	public static final String CLEF_SPACING = "pdf.clef-spacing";
	public static final String KEY_SIGNATURE_SPACING = "pdf.key-signature-spacing";
	public static final String TIME_SIGNATURE_SPACING = "pdf.time-signature-spacing";

	public PDFSettings() {
		this.settingsMap = new HashMap<String, Float>();
		this.orderedKeys = new ArrayList<String>();
		// default values (ordered for dialog)
		this.addSetting(FIRST_MEASURE_SPACING, 15f);
		this.addSetting(MIN_BUFFER_SEPARATOR, 15f);
		this.addSetting(TOP_SPACING, 20f);
		this.addSetting(MIN_SCORE_TAB_SPACING, 15f);
		this.addSetting(SCORE_LINE_SPACING, 7f);
		this.addSetting(FIRST_TRACK_SPACING, 5f);
		this.addSetting(STRING_SPACING, 8f);
		this.addSetting(CHORD_FRET_INDEX_SPACING, 8f);
		this.addSetting(CHORD_STRING_SPACING,4f);
		this.addSetting(CHORD_FRET_SPACING, 5f);
		this.addSetting(CHORD_NOTE_SIZE, 3f);
		this.addSetting(CHORD_LINE_WIDTH, 0f);
		this.addSetting(REPEAT_ENDING_SPACING, 20f);
		this.addSetting(TEXT_SPACING, 15f);
		this.addSetting(TRACK_SPACING, 5f);
		this.addSetting(MARKER_SPACING, 15f);
		this.addSetting(DIVISION_TYPE_SPACING, 10f);
		this.addSetting(EFFECT_SPACING, 8f);
		this.addSetting(FIRST_NOTE_SPACING, 10f);
		this.addSetting(MEASURE_LEFT_SPACING, 15f);
		this.addSetting(MEASURE_RIGHT_SPACING, 15f);
		this.addSetting(CLEF_SPACING, 30f);
		this.addSetting(KEY_SIGNATURE_SPACING, 15f);
		this.addSetting(TIME_SIGNATURE_SPACING, 15f);
	}

	public Map <String,Float> getSettingsMap() {
		return this.settingsMap;
	}

	private void addSetting(String key, float defaultValue) {
		this.orderedKeys.add(key);
		this.settingsMap.put(key, defaultValue);
	}

	public List<String> getOrderedKeys() {
		return this.orderedKeys;
	}
}
