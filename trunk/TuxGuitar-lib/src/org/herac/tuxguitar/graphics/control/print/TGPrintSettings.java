package org.herac.tuxguitar.graphics.control.print;

import org.herac.tuxguitar.graphics.control.TGLayout;

public class TGPrintSettings {
	
	public static final int ALL_TRACKS = -1;
	
	private int trackNumber;
	
	private int fromMeasure;
	
	private int toMeasure;
	
	private int style;
	
	public TGPrintSettings() {
		this(ALL_TRACKS, -1, -1, TGLayout.DISPLAY_TABLATURE | TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_CHORD_DIAGRAM | TGLayout.DISPLAY_COMPACT | TGLayout.DISPLAY_MODE_BLACK_WHITE);
	}
	
	public TGPrintSettings(int trackNumber,int fromMeasure, int toMeasure, int style) {
		this.trackNumber = trackNumber;
		this.fromMeasure = fromMeasure;
		this.toMeasure = toMeasure;
		this.style = style;
	}
	
	public int getFromMeasure() {
		return this.fromMeasure;
	}
	
	public void setFromMeasure(int fromMeasure) {
		this.fromMeasure = fromMeasure;
	}
	
	public int getStyle() {
		return this.style;
	}
	
	public void setStyle(int style) {
		this.style = style;
	}
	
	public int getToMeasure() {
		return this.toMeasure;
	}
	
	public void setToMeasure(int toMeasure) {
		this.toMeasure = toMeasure;
	}
	
	public int getTrackNumber() {
		return this.trackNumber;
	}
	
	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}
}
