package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFontModel;

public class TGLayoutStyles {
	
	private boolean bufferEnabled;
	private boolean tabNotePathRendererEnabled;
	private float minBufferSeparator;
	private float minTopSpacing;
	private float minScoreTabSpacing;
	private float stringSpacing;
	private float scoreLineSpacing;
	private float trackSpacing;
	private float firstTrackSpacing;
	private float firstMeasureSpacing;
	private float firstNoteSpacing;
	private float measureLeftSpacing;
	private float measureRightSpacing;
	private float clefSpacing;
	private float keySignatureSpacing;
	private float timeSignatureSpacing;
	private float chordFretIndexSpacing;
	private float chordStringSpacing;
	private float chordFretSpacing;
	private float chordNoteSize;
	private float chordLineWidth;
	private float repeatEndingSpacing;
	private float effectSpacing;
	private float divisionTypeSpacing;
	private float textSpacing;
	private float markerSpacing;
	private float loopMarkerSpacing;
	private float[] lineWidths;
	private float[] durationWidths;
	private UIFontModel defaultFont;
	private UIFontModel noteFont;
	private UIFontModel timeSignatureFont;
	private UIFontModel lyricFont;
	private UIFontModel textFont;
	private UIFontModel markerFont;
	private UIFontModel graceFont;
	private UIFontModel chordFont;
	private UIFontModel chordFretFont;
	private UIColorModel backgroundColor;
	private UIColorModel lineColor;
	private UIColorModel scoreNoteColor;
	private UIColorModel tabNoteColor;
	private UIColorModel playNoteColor;
	private UIColorModel loopSMarkerColor;
	private UIColorModel loopEMarkerColor;
	
	public TGLayoutStyles() {
		super();
	}

	public boolean isBufferEnabled() {
		return bufferEnabled;
	}

	public void setBufferEnabled(boolean bufferEnabled) {
		this.bufferEnabled = bufferEnabled;
	}

	public boolean isTabNotePathRendererEnabled() {
		return tabNotePathRendererEnabled;
	}

	public void setTabNotePathRendererEnabled(boolean tabNotePathRendererEnabled) {
		this.tabNotePathRendererEnabled = tabNotePathRendererEnabled;
	}

	public float getMinBufferSeparator() {
		return minBufferSeparator;
	}

	public void setMinBufferSeparator(float minBufferSeparator) {
		this.minBufferSeparator = minBufferSeparator;
	}

	public float getMinTopSpacing() {
		return minTopSpacing;
	}

	public void setMinTopSpacing(float minTopSpacing) {
		this.minTopSpacing = minTopSpacing;
	}

	public float getMinScoreTabSpacing() {
		return minScoreTabSpacing;
	}

	public void setMinScoreTabSpacing(float minScoreTabSpacing) {
		this.minScoreTabSpacing = minScoreTabSpacing;
	}

	public float getStringSpacing() {
		return stringSpacing;
	}

	public void setStringSpacing(float stringSpacing) {
		this.stringSpacing = stringSpacing;
	}

	public float getScoreLineSpacing() {
		return scoreLineSpacing;
	}

	public void setScoreLineSpacing(float scoreLineSpacing) {
		this.scoreLineSpacing = scoreLineSpacing;
	}

	public float getTrackSpacing() {
		return trackSpacing;
	}

	public void setTrackSpacing(float trackSpacing) {
		this.trackSpacing = trackSpacing;
	}

	public float getFirstTrackSpacing() {
		return firstTrackSpacing;
	}

	public void setFirstTrackSpacing(float firstTrackSpacing) {
		this.firstTrackSpacing = firstTrackSpacing;
	}

	public float getFirstMeasureSpacing() {
		return firstMeasureSpacing;
	}

	public void setFirstMeasureSpacing(float firstMeasureSpacing) {
		this.firstMeasureSpacing = firstMeasureSpacing;
	}

	public float getChordFretIndexSpacing() {
		return chordFretIndexSpacing;
	}

	public float getFirstNoteSpacing() {
		return firstNoteSpacing;
	}

	public void setFirstNoteSpacing(float firstNoteSpacing) {
		this.firstNoteSpacing = firstNoteSpacing;
	}

	public float getMeasureLeftSpacing() {
		return measureLeftSpacing;
	}

	public void setMeasureLeftSpacing(float measureLeftSpacing) {
		this.measureLeftSpacing = measureLeftSpacing;
	}

	public float getMeasureRightSpacing() {
		return measureRightSpacing;
	}

	public void setMeasureRightSpacing(float measureRightSpacing) {
		this.measureRightSpacing = measureRightSpacing;
	}

	public float getClefSpacing() {
		return clefSpacing;
	}

	public void setClefSpacing(float clefSpacing) {
		this.clefSpacing = clefSpacing;
	}

	public float getKeySignatureSpacing() {
		return keySignatureSpacing;
	}

	public void setKeySignatureSpacing(float keySignatureSpacing) {
		this.keySignatureSpacing = keySignatureSpacing;
	}

	public float getTimeSignatureSpacing() {
		return timeSignatureSpacing;
	}

	public void setTimeSignatureSpacing(float timeSignatureSpacing) {
		this.timeSignatureSpacing = timeSignatureSpacing;
	}

	public void setChordFretIndexSpacing(float chordFretIndexSpacing) {
		this.chordFretIndexSpacing = chordFretIndexSpacing;
	}

	public float getChordStringSpacing() {
		return chordStringSpacing;
	}

	public void setChordStringSpacing(float chordStringSpacing) {
		this.chordStringSpacing = chordStringSpacing;
	}

	public float getChordFretSpacing() {
		return chordFretSpacing;
	}

	public void setChordFretSpacing(float chordFretSpacing) {
		this.chordFretSpacing = chordFretSpacing;
	}

	public float getChordNoteSize() {
		return chordNoteSize;
	}

	public void setChordNoteSize(float chordNoteSize) {
		this.chordNoteSize = chordNoteSize;
	}

	public float getChordLineWidth() {
		return chordLineWidth;
	}

	public void setChordLineWidth(float chordLineWidth) {
		this.chordLineWidth = chordLineWidth;
	}

	public float getRepeatEndingSpacing() {
		return repeatEndingSpacing;
	}

	public void setRepeatEndingSpacing(float repeatEndingSpacing) {
		this.repeatEndingSpacing = repeatEndingSpacing;
	}

	public float getEffectSpacing() {
		return effectSpacing;
	}

	public void setEffectSpacing(float effectSpacing) {
		this.effectSpacing = effectSpacing;
	}

	public float getDivisionTypeSpacing() {
		return divisionTypeSpacing;
	}

	public void setDivisionTypeSpacing(float divisionTypeSpacing) {
		this.divisionTypeSpacing = divisionTypeSpacing;
	}

	public float getTextSpacing() {
		return textSpacing;
	}

	public void setTextSpacing(float textSpacing) {
		this.textSpacing = textSpacing;
	}

	public float getMarkerSpacing() {
		return markerSpacing;
	}

	public void setMarkerSpacing(float markerSpacing) {
		this.markerSpacing = markerSpacing;
	}

	public float getLoopMarkerSpacing() {
		return loopMarkerSpacing;
	}

	public void setLoopMarkerSpacing(float loopMarkerSpacing) {
		this.loopMarkerSpacing = loopMarkerSpacing;
	}

	public float[] getLineWidths() {
		return lineWidths;
	}

	public void setLineWidths(float[] lineWidths) {
		this.lineWidths = lineWidths;
	}

	public float[] getDurationWidths() {
		return durationWidths;
	}

	public void setDurationWidths(float[] durationWidths) {
		this.durationWidths = durationWidths;
	}
	
	public UIFontModel getDefaultFont() {
		return defaultFont;
	}

	public void setDefaultFont(UIFontModel defaultFont) {
		this.defaultFont = defaultFont;
	}

	public UIFontModel getNoteFont() {
		return noteFont;
	}

	public void setNoteFont(UIFontModel noteFont) {
		this.noteFont = noteFont;
	}

	public UIFontModel getTimeSignatureFont() {
		return timeSignatureFont;
	}

	public void setTimeSignatureFont(UIFontModel timeSignatureFont) {
		this.timeSignatureFont = timeSignatureFont;
	}

	public UIFontModel getLyricFont() {
		return lyricFont;
	}

	public void setLyricFont(UIFontModel lyricFont) {
		this.lyricFont = lyricFont;
	}

	public UIFontModel getTextFont() {
		return textFont;
	}

	public void setTextFont(UIFontModel textFont) {
		this.textFont = textFont;
	}

	public UIFontModel getMarkerFont() {
		return markerFont;
	}

	public void setMarkerFont(UIFontModel markerFont) {
		this.markerFont = markerFont;
	}

	public UIFontModel getGraceFont() {
		return graceFont;
	}

	public void setGraceFont(UIFontModel graceFont) {
		this.graceFont = graceFont;
	}

	public UIFontModel getChordFont() {
		return chordFont;
	}

	public void setChordFont(UIFontModel chordFont) {
		this.chordFont = chordFont;
	}

	public UIFontModel getChordFretFont() {
		return chordFretFont;
	}

	public void setChordFretFont(UIFontModel chordFretFont) {
		this.chordFretFont = chordFretFont;
	}

	public UIColorModel getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(UIColorModel backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public UIColorModel getLineColor() {
		return lineColor;
	}

	public void setLineColor(UIColorModel lineColor) {
		this.lineColor = lineColor;
	}

	public UIColorModel getScoreNoteColor() {
		return scoreNoteColor;
	}

	public void setScoreNoteColor(UIColorModel scoreNoteColor) {
		this.scoreNoteColor = scoreNoteColor;
	}

	public UIColorModel getTabNoteColor() {
		return tabNoteColor;
	}

	public void setTabNoteColor(UIColorModel tabNoteColor) {
		this.tabNoteColor = tabNoteColor;
	}

	public UIColorModel getPlayNoteColor() {
		return playNoteColor;
	}

	public void setPlayNoteColor(UIColorModel playNoteColor) {
		this.playNoteColor = playNoteColor;
	}

	public UIColorModel getLoopSMarkerColor() {
		return loopSMarkerColor;
	}

	public void setLoopSMarkerColor(UIColorModel loopSMarkerColor) {
		this.loopSMarkerColor = loopSMarkerColor;
	}

	public UIColorModel getLoopEMarkerColor() {
		return loopEMarkerColor;
	}

	public void setLoopEMarkerColor(UIColorModel loopEMarkerColor) {
		this.loopEMarkerColor = loopEMarkerColor;
	}
}
