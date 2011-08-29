package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFontModel;

public class TGLayoutStyles {
	
	private boolean bufferEnabled;
	private int minBufferSeparator;
	private int minTopSpacing;
	private int minScoreTabSpacing;
	private int stringSpacing;
	private int scoreLineSpacing;
	private int trackSpacing;
	private int firstTrackSpacing;
	private int firstMeasureSpacing;
	private int chordFretIndexSpacing;
	private int chordStringSpacing;
	private int chordFretSpacing;
	private int chordNoteSize;
	private int repeatEndingSpacing;
	private int effectSpacing;
	private int divisionTypeSpacing;
	private int textSpacing;
	private int markerSpacing;
	private int loopMarkerSpacing;
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
	
	public boolean isBufferEnabled() {
		return this.bufferEnabled;
	}
	
	public void setBufferEnabled(boolean bufferEnabled) {
		this.bufferEnabled = bufferEnabled;
	}
	
	public int getMinBufferSeparator() {
		return this.minBufferSeparator;
	}
	
	public void setMinBufferSeparator(int minBufferSeparator) {
		this.minBufferSeparator = minBufferSeparator;
	}
	
	public int getMinTopSpacing() {
		return this.minTopSpacing;
	}
	
	public void setMinTopSpacing(int minTopSpacing) {
		this.minTopSpacing = minTopSpacing;
	}
	
	public int getMinScoreTabSpacing() {
		return this.minScoreTabSpacing;
	}
	
	public void setMinScoreTabSpacing(int minScoreTabSpacing) {
		this.minScoreTabSpacing = minScoreTabSpacing;
	}
	
	public int getStringSpacing() {
		return this.stringSpacing;
	}
	
	public void setStringSpacing(int stringSpacing) {
		this.stringSpacing = stringSpacing;
	}
	
	public int getScoreLineSpacing() {
		return this.scoreLineSpacing;
	}
	
	public void setScoreLineSpacing(int scoreLineSpacing) {
		this.scoreLineSpacing = scoreLineSpacing;
	}
	
	public int getTrackSpacing() {
		return this.trackSpacing;
	}
	
	public void setTrackSpacing(int trackSpacing) {
		this.trackSpacing = trackSpacing;
	}
	
	public int getFirstTrackSpacing() {
		return this.firstTrackSpacing;
	}
	
	public void setFirstTrackSpacing(int firstTrackSpacing) {
		this.firstTrackSpacing = firstTrackSpacing;
	}
	
	public int getFirstMeasureSpacing() {
		return this.firstMeasureSpacing;
	}
	
	public void setFirstMeasureSpacing(int firstMeasureSpacing) {
		this.firstMeasureSpacing = firstMeasureSpacing;
	}
	
	public int getChordFretIndexSpacing() {
		return this.chordFretIndexSpacing;
	}
	
	public void setChordFretIndexSpacing(int chordFretIndexSpacing) {
		this.chordFretIndexSpacing = chordFretIndexSpacing;
	}
	
	public int getChordStringSpacing() {
		return this.chordStringSpacing;
	}
	
	public void setChordStringSpacing(int chordStringSpacing) {
		this.chordStringSpacing = chordStringSpacing;
	}
	
	public int getChordFretSpacing() {
		return this.chordFretSpacing;
	}
	
	public void setChordFretSpacing(int chordFretSpacing) {
		this.chordFretSpacing = chordFretSpacing;
	}
	
	public int getChordNoteSize() {
		return this.chordNoteSize;
	}
	
	public void setChordNoteSize(int chordNoteSize) {
		this.chordNoteSize = chordNoteSize;
	}
	
	public int getRepeatEndingSpacing() {
		return this.repeatEndingSpacing;
	}
	
	public void setRepeatEndingSpacing(int repeatEndingSpacing) {
		this.repeatEndingSpacing = repeatEndingSpacing;
	}
	
	public int getEffectSpacing() {
		return this.effectSpacing;
	}
	
	public void setEffectSpacing(int effectSpacing) {
		this.effectSpacing = effectSpacing;
	}
	
	public int getDivisionTypeSpacing() {
		return this.divisionTypeSpacing;
	}
	
	public void setDivisionTypeSpacing(int divisionTypeSpacing) {
		this.divisionTypeSpacing = divisionTypeSpacing;
	}
	
	public int getTextSpacing() {
		return this.textSpacing;
	}
	
	public void setTextSpacing(int textSpacing) {
		this.textSpacing = textSpacing;
	}
	
	public int getMarkerSpacing() {
		return this.markerSpacing;
	}
	
	public void setMarkerSpacing(int markerSpacing) {
		this.markerSpacing = markerSpacing;
	}
	
	public int getLoopMarkerSpacing() {
		return this.loopMarkerSpacing;
	}
	
	public void setLoopMarkerSpacing(int loopMarkerSpacing) {
		this.loopMarkerSpacing = loopMarkerSpacing;
	}
	
	public TGFontModel getDefaultFont() {
		return this.defaultFont;
	}
	
	public void setDefaultFont(TGFontModel defaultFont) {
		this.defaultFont = defaultFont;
	}
	
	public TGFontModel getNoteFont() {
		return this.noteFont;
	}
	
	public void setNoteFont(TGFontModel noteFont) {
		this.noteFont = noteFont;
	}
	
	public TGFontModel getTimeSignatureFont() {
		return this.timeSignatureFont;
	}
	
	public void setTimeSignatureFont(TGFontModel timeSignatureFont) {
		this.timeSignatureFont = timeSignatureFont;
	}
	
	public TGFontModel getLyricFont() {
		return this.lyricFont;
	}
	
	public void setLyricFont(TGFontModel lyricFont) {
		this.lyricFont = lyricFont;
	}
	
	public TGFontModel getTextFont() {
		return this.textFont;
	}
	
	public void setTextFont(TGFontModel textFont) {
		this.textFont = textFont;
	}
	
	public TGFontModel getMarkerFont() {
		return this.markerFont;
	}
	
	public void setMarkerFont(TGFontModel markerFont) {
		this.markerFont = markerFont;
	}
	
	public TGFontModel getGraceFont() {
		return this.graceFont;
	}
	
	public void setGraceFont(TGFontModel graceFont) {
		this.graceFont = graceFont;
	}
	
	public TGFontModel getChordFont() {
		return this.chordFont;
	}
	
	public void setChordFont(TGFontModel chordFont) {
		this.chordFont = chordFont;
	}
	
	public TGFontModel getChordFretFont() {
		return this.chordFretFont;
	}
	
	public void setChordFretFont(TGFontModel chordFretFont) {
		this.chordFretFont = chordFretFont;
	}
	
	public TGColorModel getBackgroundColor() {
		return this.backgroundColor;
	}
	
	public void setBackgroundColor(TGColorModel backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public TGColorModel getLineColor() {
		return this.lineColor;
	}
	
	public void setLineColor(TGColorModel lineColor) {
		this.lineColor = lineColor;
	}
	
	public TGColorModel getScoreNoteColor() {
		return this.scoreNoteColor;
	}
	
	public void setScoreNoteColor(TGColorModel scoreNoteColor) {
		this.scoreNoteColor = scoreNoteColor;
	}
	
	public TGColorModel getTabNoteColor() {
		return this.tabNoteColor;
	}
	
	public void setTabNoteColor(TGColorModel tabNoteColor) {
		this.tabNoteColor = tabNoteColor;
	}
	
	public TGColorModel getPlayNoteColor() {
		return this.playNoteColor;
	}
	
	public void setPlayNoteColor(TGColorModel playNoteColor) {
		this.playNoteColor = playNoteColor;
	}
	
	public TGColorModel getLoopSMarkerColor() {
		return this.loopSMarkerColor;
	}
	
	public void setLoopSMarkerColor(TGColorModel loopSMarkerColor) {
		this.loopSMarkerColor = loopSMarkerColor;
	}
	
	public TGColorModel getLoopEMarkerColor() {
		return this.loopEMarkerColor;
	}
	
	public void setLoopEMarkerColor(TGColorModel loopEMarkerColor) {
		this.loopEMarkerColor = loopEMarkerColor;
	}
}
