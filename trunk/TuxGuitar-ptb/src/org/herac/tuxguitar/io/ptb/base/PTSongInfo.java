package org.herac.tuxguitar.io.ptb.base;

public class PTSongInfo {
	private int classification;
	private int releaseType;
	private int albumType;
	private int day;
	private int month;
	private int year;
	private int style;
	private int level;
	private boolean liveRecording;
	private String name;
	private String interpret;
	private String album;
	private String author;
	private String lyricist;
	private String arrenger;
	private String guitarTranscriber;
	private String bassTranscriber;
	private String lyrics;
	private String guitarInstructions;
	private String bassInstructions;
	private String instructions;
	private String copyright;
	
	public PTSongInfo(){
		super();
	}
	
	public String getAlbum() {
		return this.album;
	}
	
	public void setAlbum(String album) {
		this.album = album;
	}
	
	public int getAlbumType() {
		return this.albumType;
	}
	
	public void setAlbumType(int albumType) {
		this.albumType = albumType;
	}
	
	public String getArrenger() {
		return this.arrenger;
	}
	
	public void setArrenger(String arrenger) {
		this.arrenger = arrenger;
	}
	
	public String getAuthor() {
		return this.author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getBassInstructions() {
		return this.bassInstructions;
	}
	
	public void setBassInstructions(String bassInstructions) {
		this.bassInstructions = bassInstructions;
	}
	
	public String getBassTranscriber() {
		return this.bassTranscriber;
	}
	
	public void setBassTranscriber(String bassTranscriber) {
		this.bassTranscriber = bassTranscriber;
	}
	
	public int getClassification() {
		return this.classification;
	}
	
	public void setClassification(int classification) {
		this.classification = classification;
	}
	
	public String getCopyright() {
		return this.copyright;
	}
	
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	
	public int getDay() {
		return this.day;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	public String getGuitarInstructions() {
		return this.guitarInstructions;
	}
	
	public void setGuitarInstructions(String guitarInstructions) {
		this.guitarInstructions = guitarInstructions;
	}
	
	public String getGuitarTranscriber() {
		return this.guitarTranscriber;
	}
	
	public void setGuitarTranscriber(String guitarTranscriber) {
		this.guitarTranscriber = guitarTranscriber;
	}
	
	public String getInstructions() {
		return this.instructions;
	}
	
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	
	public String getInterpret() {
		return this.interpret;
	}
	
	public void setInterpret(String interpret) {
		this.interpret = interpret;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public boolean isLiveRecording() {
		return this.liveRecording;
	}
	
	public void setLiveRecording(boolean liveRecording) {
		this.liveRecording = liveRecording;
	}
	
	public String getLyricist() {
		return this.lyricist;
	}
	
	public void setLyricist(String lyricist) {
		this.lyricist = lyricist;
	}
	
	public String getLyrics() {
		return this.lyrics;
	}
	
	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}
	
	public int getMonth() {
		return this.month;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getReleaseType() {
		return this.releaseType;
	}
	
	public void setReleaseType(int releaseType) {
		this.releaseType = releaseType;
	}
	
	public int getStyle() {
		return this.style;
	}
	
	public void setStyle(int style) {
		this.style = style;
	}
	
	public int getYear() {
		return this.year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public void copy( PTSongInfo info ){
		info.setClassification(getClassification());
		info.setReleaseType(getReleaseType());
		info.setAlbumType(getAlbumType());
		info.setDay(getDay());
		info.setMonth(getMonth());
		info.setYear(getYear());
		info.setStyle(getStyle());
		info.setLevel(getLevel());
		info.setLiveRecording(isLiveRecording());
		info.setName(getName());
		info.setInterpret(getInterpret());
		info.setAlbum(getAlbum());
		info.setAuthor(getAuthor());
		info.setLyricist(getLyricist());
		info.setArrenger(getArrenger());
		info.setGuitarTranscriber(getGuitarTranscriber());
		info.setBassTranscriber(getBassTranscriber());
		info.setLyrics(getLyrics());
		info.setGuitarInstructions(getGuitarInstructions());
		info.setBassInstructions(getBassInstructions());
		info.setInstructions(getInstructions());
		info.setCopyright(getCopyright());
	}
}
