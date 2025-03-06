package org.herac.tuxguitar.io.gpx.score;

public class GPXTrack {

	private int id;
	private int[] tuningPitches;
	private int[] color;
	private String name;

	private int gmProgram;
	private int gmChannel1;
	private int gmChannel2;

	public GPXTrack(){
		super();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int[] getTuningPitches() {
		return this.tuningPitches;
	}

	public void setTuningPitches(int[] tuningPitches) {
		this.tuningPitches = tuningPitches;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getColor() {
		return color;
	}

	public void setColor(int[] color) {
		this.color = color;
	}

	public int getGmProgram() {
		return gmProgram;
	}

	public void setGmProgram(int gmProgram) {
		this.gmProgram = gmProgram;
	}

	public int getGmChannel1() {
		return gmChannel1;
	}

	public void setGmChannel1(int gmChannel1) {
		this.gmChannel1 = gmChannel1;
	}

	public int getGmChannel2() {
		return gmChannel2;
	}

	public void setGmChannel2(int gmChannel2) {
		this.gmChannel2 = gmChannel2;
	}
}
