package org.herac.tuxguitar.gui.printer;

import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;

public class PrintStyles {

	private int trackNumber;
	
	private int fromMeasure;
	
	private int toMeasure;
	
	private int style;
	
	public PrintStyles() {
		this(-1,-1,-1,ViewLayout.DISPLAY_TABLATURE);
	}	
	
	public PrintStyles(int trackNumber,int fromMeasure, int toMeasure, int style) {
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
