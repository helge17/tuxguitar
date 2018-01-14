package org.herac.tuxguitar.io.gpx.score;

public class GPXChord {
	
	private int id;
	private String name;
	private Integer stringCount;
	private Integer fretCount;
	private Integer baseFret;
	private Integer[] frets;
	
	public GPXChord(){
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStringCount() {
		return stringCount;
	}

	public void setStringCount(Integer stringCount) {
		this.stringCount = stringCount;
	}

	public Integer getFretCount() {
		return fretCount;
	}

	public void setFretCount(Integer fretCount) {
		this.fretCount = fretCount;
	}

	public Integer getBaseFret() {
		return baseFret;
	}

	public void setBaseFret(Integer baseFret) {
		this.baseFret = baseFret;
	}

	public Integer[] getFrets() {
		return frets;
	}

	public void setFrets(Integer[] frets) {
		this.frets = frets;
	}
}
