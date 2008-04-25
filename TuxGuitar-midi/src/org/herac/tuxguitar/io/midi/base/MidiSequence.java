package org.herac.tuxguitar.io.midi.base;

import java.util.ArrayList;
import java.util.List;

public class MidiSequence {
	
	public static final float PPQ = 0.0f;
	public static final float SMPTE_24 = 24.0f;
	public static final float SMPTE_25 = 25.0f;
	public static final float SMPTE_30DROP = 29.97f;
	public static final float SMPTE_30 = 30.0f;
	
	protected float divisionType;
	protected int resolution;
	private List tracks;
	
	public MidiSequence(float divisionType, int resolution){
		this.divisionType = divisionType;
		this.resolution = resolution;
		this.tracks = new ArrayList();
	}
	
	public void addTrack(MidiTrack track){
		this.tracks.add(track);
	}
	
	public MidiTrack getTrack(int index){
		return (MidiTrack)this.tracks.get(index);
	}
	
	public int countTracks(){
		return this.tracks.size();
	}
	
	public float getDivisionType() {
		return this.divisionType;
	}
	
	public int getResolution() {
		return this.resolution;
	}
	
	public void sort(){
		for(int i = 0; i < this.tracks.size(); i ++){
			MidiTrack track = (MidiTrack)this.tracks.get(i);
			track.sort();
		}
	}
	
	public void finish(){
		for(int i = 0; i < this.tracks.size(); i ++){
			MidiTrack track = (MidiTrack)this.tracks.get(i);
			track.add(new MidiEvent(MidiMessage.metaMessage(47,new byte[]{}),track.ticks()));
			track.sort();
		}
	}
}
 