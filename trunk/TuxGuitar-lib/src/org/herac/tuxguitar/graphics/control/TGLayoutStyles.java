package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFontModel;

public class TGLayoutStyles {
	
	private boolean bufferEnabled;
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
	private TGFontModel defaultFont;
	private TGFontModel noteFont;
	private TGFontModel timeSignatureFont;
	private TGFontModel lyricFont;
	private TGFontModel textFont;
	private TGFontModel markerFont;
	private TGFontModel graceFont;
	private TGFontModel chordFont;
	private TGFontModel chordFretFont;
	private TGColorModel backgroundColor;
	private TGColorModel lineColor;
	private TGColorModel scoreNoteColor;
	private TGColorModel tabNoteColor;
	private TGColorModel playNoteColor;
	private TGColorModel loopSMarkerColor;
	private TGColorModel loopEMarkerColor;
	
	public TGLayoutStyles() {
		super();
	}

	public boolean isBufferEnabled() {
		return bufferEnabled;
	}

	public void setBufferEnabled(boolean bufferEnabled) {
		this.bufferEnabled = bufferEnabled;
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
	
	public TGFontModel getDefaultFont() {
		return defaultFont;
	}

	public void setDefaultFont(TGFontModel defaultFont) {
		this.defaultFont = defaultFont;
	}

	public TGFontModel getNoteFont() {
		return noteFont;
	}

	public void setNoteFont(TGFontModel noteFont) {
		this.noteFont = noteFont;
	}

	public TGFontModel getTimeSignatureFont() {
		return timeSignatureFont;
	}

	public void setTimeSignatureFont(TGFontModel timeSignatureFont) {
		this.timeSignatureFont = timeSignatureFont;
	}

	public TGFontModel getLyricFont() {
		return lyricFont;
	}

	public void setLyricFont(TGFontModel lyricFont) {
		this.lyricFont = lyricFont;
	}

	public TGFontModel getTextFont() {
		return textFont;
	}

	public void setTextFont(TGFontModel textFont) {
		this.textFont = textFont;
	}

	public TGFontModel getMarkerFont() {
		return markerFont;
	}

	public void setMarkerFont(TGFontModel markerFont) {
		this.markerFont = markerFont;
	}

	public TGFontModel getGraceFont() {
		return graceFont;
	}

	public void setGraceFont(TGFontModel graceFont) {
		this.graceFont = graceFont;
	}

	public TGFontModel getChordFont() {
		return chordFont;
	}

	public void setChordFont(TGFontModel chordFont) {
		this.chordFont = chordFont;
	}

	public TGFontModel getChordFretFont() {
		return chordFretFont;
	}

	public void setChordFretFont(TGFontModel chordFretFont) {
		this.chordFretFont = chordFretFont;
	}

	public TGColorModel getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(TGColorModel backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public TGColorModel getLineColor() {
		return lineColor;
	}

	public void setLineColor(TGColorModel lineColor) {
		this.lineColor = lineColor;
	}

	public TGColorModel getScoreNoteColor() {
		return scoreNoteColor;
	}

	public void setScoreNoteColor(TGColorModel scoreNoteColor) {
		this.scoreNoteColor = scoreNoteColor;
	}

	public TGColorModel getTabNoteColor() {
		return tabNoteColor;
	}

	public void setTabNoteColor(TGColorModel tabNoteColor) {
		this.tabNoteColor = tabNoteColor;
	}

	public TGColorModel getPlayNoteColor() {
		return playNoteColor;
	}

	public void setPlayNoteColor(TGColorModel playNoteColor) {
		this.playNoteColor = playNoteColor;
	}

	public TGColorModel getLoopSMarkerColor() {
		return loopSMarkerColor;
	}

	public void setLoopSMarkerColor(TGColorModel loopSMarkerColor) {
		this.loopSMarkerColor = loopSMarkerColor;
	}

	public TGColorModel getLoopEMarkerColor() {
		return loopEMarkerColor;
	}

	public void setLoopEMarkerColor(TGColorModel loopEMarkerColor) {
		this.loopEMarkerColor = loopEMarkerColor;
	}
}
