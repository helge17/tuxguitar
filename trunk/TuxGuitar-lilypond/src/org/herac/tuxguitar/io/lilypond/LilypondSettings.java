package org.herac.tuxguitar.io.lilypond;

public class LilypondSettings {
	
	public static final int ALL_TRACKS = -1;
	
	public static final int FIRST_MEASURE = -1;
	
	public static final int LAST_MEASURE = -1;
	
	private int track;
	
	private int measureFrom;
	
	private int measureTo;
	
	private boolean trackNameEnabled;
	
	private boolean trackGroupEnabled;
	
	public LilypondSettings(){
		super();
	}
	
	public int getMeasureFrom() {
		return this.measureFrom;
	}
	
	public void setMeasureFrom(int measureFrom) {
		this.measureFrom = measureFrom;
	}
	
	public int getMeasureTo() {
		return this.measureTo;
	}
	
	public void setMeasureTo(int measureTo) {
		this.measureTo = measureTo;
	}
	
	public int getTrack() {
		return this.track;
	}
	
	public void setTrack(int track) {
		this.track = track;
	}
	
	public boolean isTrackGroupEnabled() {
		return this.trackGroupEnabled;
	}
	
	public void setTrackGroupEnabled(boolean trackGroupEnabled) {
		this.trackGroupEnabled = trackGroupEnabled;
	}
	
	public boolean isTrackNameEnabled() {
		return this.trackNameEnabled;
	}
	
	public void setTrackNameEnabled(boolean trackNameEnabled) {
		this.trackNameEnabled = trackNameEnabled;
	}
	
	public static LilypondSettings getDefaults(){
		LilypondSettings settings = new LilypondSettings();
		settings.setTrack(ALL_TRACKS);
		settings.setMeasureFrom(FIRST_MEASURE);
		settings.setMeasureTo(LAST_MEASURE);
		settings.setTrackGroupEnabled(true);
		settings.setTrackNameEnabled(false);
		return settings;
	}
}