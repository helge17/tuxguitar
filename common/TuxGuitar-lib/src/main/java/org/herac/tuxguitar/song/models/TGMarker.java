package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class TGMarker {
	private static final TGColor DEFAULT_COLOR = TGColor.RED;
	private static final String DEFAULT_TITLE = "Untitled";
	
	private int measure;
	private String title;
	private TGColor color;
	
	public TGMarker(TGFactory factory) {
		this.measure = 0;
		this.title = DEFAULT_TITLE;
		this.color = DEFAULT_COLOR.clone(factory);
	}
	
	public int getMeasure() {
		return this.measure;
	}
	
	public void setMeasure(int measure) {
		this.measure = measure;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public TGColor getColor() {
		return this.color;
	}
	
	public void setColor(TGColor color) {
		this.color = color;
	}
	
	public TGMarker clone(TGFactory factory){
		TGMarker tgMarker = factory.newMarker();
		tgMarker.copyFrom(this);
		return tgMarker;
	}
	
	public void copyFrom(TGMarker marker){
		this.setMeasure(marker.getMeasure());
		this.setTitle(marker.getTitle());
		this.getColor().copyFrom(marker.getColor());
	}
}
