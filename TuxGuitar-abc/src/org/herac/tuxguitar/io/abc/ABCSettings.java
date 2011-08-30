package org.herac.tuxguitar.io.abc;

public class ABCSettings {
	
	public static final int ALL_TRACKS = -1;
	public static final int FIRST_MEASURE = -1;
	public static final int LAST_MEASURE = -1;
	public static final int AUTO_MEASURES = 0;
	public static final int AUTO_TRACK = 0;
	public static final int NO_TRACK = -2;
	
	private int x = 1;
	private int track;
	private int measureFrom;	
	private int measureTo;
	private int measuresPerLine;
	private int diagramTrack;
	private int chordTrack;
	private int baseTrack;
	private int droneTrack;

	private boolean instrumentsStartAt1;
	private boolean trackGroupEnabled;
	private boolean trackNameEnabled;
	private boolean scoreEnabled;
	private boolean tablatureEnabled;
	private boolean lyricsEnabled;
	private boolean chordDiagramEnabled;
	private boolean chordEnabled;
	private boolean droneEnabled;
	private boolean textEnabled;
	
	public ABCSettings(){
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
	
	public static ABCSettings getDefaults(){
		ABCSettings settings = new ABCSettings();
		settings.setX(1);
		settings.setInstrumentsStartAt1(true);
		settings.setTrack(ALL_TRACKS);
		settings.setBaseTrack(AUTO_TRACK);
		settings.setChordTrack(AUTO_TRACK);
		settings.setDiagramTrack(AUTO_TRACK);
		settings.setDroneTrack(NO_TRACK);
		settings.setMeasureFrom(FIRST_MEASURE);
		settings.setMeasureTo(LAST_MEASURE);
		settings.setMeasuresPerLine(AUTO_MEASURES);
		settings.setScoreEnabled(true);
		settings.setDroneEnabled(false);
		settings.setTablatureEnabled(true);
		settings.setTextEnabled(true);
		settings.setLyricsEnabled(true);
		settings.setChordDiagramEnabled(true);
		settings.setChordEnabled(true);
		settings.setTrackNameEnabled(true);
		settings.setTrackGroupEnabled(false);
		return settings;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the instrumentsStartAt1
	 */
	public boolean isInstrumentsStartAt1() {
		return instrumentsStartAt1;
	}

	/**
	 * @param instrumentsStartAt1 the instrumentsStartAt1 to set
	 */
	public void setInstrumentsStartAt1(boolean instrumentsStartAt1) {
		this.instrumentsStartAt1 = instrumentsStartAt1;
	}

	public int getMeasuresPerLine() {
		return measuresPerLine;
	}

	/**
	 * @param measuresPerLine the measuresPerLine to set
	 */
	public void setMeasuresPerLine(int measuresPerLine) {
		this.measuresPerLine = measuresPerLine;
	}

	/**
	 * @return the baseTrack
	 */
	public int getBaseTrack() {
		return baseTrack;
	}

	/**
	 * @param baseTrack the baseTrack to set
	 */
	public void setBaseTrack(int baseTrack) {
		this.baseTrack = baseTrack;
	}

	/**
	 * @return the chordTrack
	 */
	public int getChordTrack() {
		return chordTrack;
	}

	/**
	 * @param chordTrack the chordTrack to set
	 */
	public void setChordTrack(int chordTrack) {
		this.chordTrack = chordTrack;
	}

	/**
	 * @return the diagramTrack
	 */
	public int getDiagramTrack() {
		return diagramTrack;
	}

	/**
	 * @param diagramTrack the diagramTrack to set
	 */
	public void setDiagramTrack(int diagramTrack) {
		this.diagramTrack = diagramTrack;
	}

	public int getDroneTrack() {
		return droneTrack;
	}

	/**
	 * @param droneTrack the droneTrack to set
	 */
	public void setDroneTrack(int droneTrack) {
		this.droneTrack = droneTrack;
	}

	/**
	 * @return the droneEnabled
	 */
	public boolean isDroneEnabled() {
		return droneEnabled;
	}

	/**
	 * @param droneEnabled the droneEnabled to set
	 */
	public void setDroneEnabled(boolean droneEnabled) {
		this.droneEnabled = droneEnabled;
	}

	/**
	 * @return the chordEnabled
	 */
	public boolean isChordEnabled() {
		return chordEnabled;
	}

	/**
	 * @param chordEnabled the chordEnabled to set
	 */
	public void setChordEnabled(boolean chordEnabled) {
		this.chordEnabled = chordEnabled;
	}
}