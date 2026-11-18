package app.tuxguitar.io.gpx.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GPXTrack {

	private int id;
	private int[] tuningPitches;
	private int[] color;
	private String name;
	private int capo;
	private List<GPXChord> chords;

	private int gmProgram;
	private int gmChannel1;
	private int gmChannel2;

	public GPXTrack(){
		super();
		this.chords = new ArrayList<GPXChord>();
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

	public int getCapo() {
		return this.capo;
	}

	public void setCapo(int capo) {
		this.capo = capo;
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

	public List<GPXChord> getChords() {
		return chords;
	}

	public GPXChord getChord( int id ){
		Iterator<GPXChord> it = this.chords.iterator();
		while( it.hasNext() ){
			GPXChord chord = it.next();
			if( chord.getId() == id ){
				return chord;
			}
		}
		return null;
	}

}
