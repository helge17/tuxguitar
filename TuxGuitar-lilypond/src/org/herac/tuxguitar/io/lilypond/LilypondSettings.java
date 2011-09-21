package org.herac.tuxguitar.io.lilypond;

public class LilypondSettings {
	
	public static final int ALL_TRACKS = -1;
	
	public static final int FIRST_MEASURE = -1;
	
	public static final int LAST_MEASURE = -1;
	
	private String lilypondVersion;

	private int track;
	private int measureFrom;	
	private int measureTo;

	private boolean trackGroupEnabled;
	private boolean trackNameEnabled;
	private boolean scoreEnabled;
	private boolean tablatureEnabled;
	private boolean lyricsEnabled;
	private boolean chordDiagramEnabled;
	private boolean textEnabled;
	
	public LilypondSettings(){
		super();
	}
	
	public String getLilypondVersion() {
		return this.lilypondVersion;
	}
	
	public void setLilypondVersion(String lilypondVersion) {
		this.lilypondVersion = lilypondVersion;
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
	
	public boolean isScoreEnabled() {
		return this.scoreEnabled;
	}
	
	public void setScoreEnabled(boolean scoreEnabled) {
		this.scoreEnabled = scoreEnabled;
	}
	
	public boolean isTablatureEnabled() {
		return this.tablatureEnabled;
	}
	
	public void setTablatureEnabled(boolean tablatureEnabled) {
		this.tablatureEnabled = tablatureEnabled;
	}
	
	public boolean isLyricsEnabled() {
		return this.lyricsEnabled;
	}
	
	public void setLyricsEnabled(boolean lyricsEnabled) {
		this.lyricsEnabled = lyricsEnabled;
	}
	
	public boolean isChordDiagramEnabled() {
		return this.chordDiagramEnabled;
	}
	
	public void setChordDiagramEnabled(boolean chordDiagramEnabled) {
		this.chordDiagramEnabled = chordDiagramEnabled;
	}
	
	public boolean isTextEnabled() {
		return this.textEnabled;
	}
	
	public void setTextEnabled(boolean textEnabled) {
		this.textEnabled = textEnabled;
	}

	public void check(){
		if(!this.isScoreEnabled() && !this.isTablatureEnabled()){
			this.setScoreEnabled( true );
			this.setTablatureEnabled( true );
		}
	}
	
	public static LilypondSettings getDefaults(){
		LilypondSettings settings = new LilypondSettings();
		settings.setTrack(ALL_TRACKS);
		settings.setMeasureFrom(FIRST_MEASURE);
		settings.setMeasureTo(LAST_MEASURE);
		settings.setScoreEnabled(true);
		settings.setTablatureEnabled(true);
		settings.setTextEnabled(true);
		settings.setLyricsEnabled(true);
		settings.setChordDiagramEnabled(true);
		settings.setTrackNameEnabled(true);
		settings.setTrackGroupEnabled(false);
		settings.setLilypondVersion("2.14.0");
		return settings;
	}
}