package org.herac.tuxguitar.gui.printer;

import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;

public class PrintStyles {
	
	private int trackNumber;
	
	private int fromMeasure;
	
	private int toMeasure;
	
	private int style;
	
	private boolean blackAndWhite;
	
	public PrintStyles() {
		this(-1,-1,-1,ViewLayout.DISPLAY_TABLATURE, true);
	}
	
	public PrintStyles(int trackNumber,int fromMeasure, int toMeasure, int style , boolean blackAndWhite) {
		this.trackNumber = trackNumber;
		this.fromMeasure = fromMeasure;
		this.toMeasure = toMeasure;
		this.style = style;
		this.blackAndWhite = blackAndWhite;
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
	
	public boolean isBlackAndWhite() {
		return this.blackAndWhite;
	}
	
	public void setBlackAndWhite(boolean blackAndWhite) {
		this.blackAndWhite = blackAndWhite;
	}
}
