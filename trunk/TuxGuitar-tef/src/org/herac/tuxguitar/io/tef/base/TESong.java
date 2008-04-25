package org.herac.tuxguitar.io.tef.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TESong {
	
	private int strings;
	private int measures;
	private TEInfo info;
	private TETempo tempo;
	private TETimeSignature timeSignature;
	private TERepeat[] repeats;
	private TEText[] texts;
	private TEChord[] chords;
	private TEPercussion[] percussions;
	private TERhythm[] rhythms;
	private TETrack[] tracks;
	private List components;
	private List tsChanges;
	
	public TESong(){
		this.components = new ArrayList();
		this.tsChanges = new ArrayList();
	}
	
	public TERhythm[] getRhythms() {
		return this.rhythms;
	}
	
	public void setRhythms(int length) {
		this.rhythms = new TERhythm[length];
	}
	
	public void setRhythm(int index,TERhythm rhythm) {
		this.rhythms[index] = rhythm;
	}
	
	public TEPercussion[] getPercussions() {
		return this.percussions;
	}
	
	public void setPercussions(int length) {
		this.percussions = new TEPercussion[length];
	}
	
	public void setPercussion(int index,TEPercussion percussions) {
		this.percussions[index] = percussions;
	}
	
	public TEChord[] getChords() {
		return this.chords;
	}
	
	public void setChords(int length) {
		this.chords = new TEChord[length];
	}
	
	public void setChord(int index,TEChord chord) {
		this.chords[index] = chord;
	}
	
	public TEInfo getInfo() {
		return this.info;
	}
	
	public void setInfo(TEInfo info) {
		this.info = info;
	}
	
	public TERepeat[] getRepeats() {
		return this.repeats;
	}
	
	public void setRepeats(int length) {
		this.repeats = new TERepeat[length];
	}
	
	public void setRepeat(int index,TERepeat repeat) {
		this.repeats[index] = repeat;
	}
	
	public TEText[] getTexts() {
		return this.texts;
	}
	
	public void setTexts(int length) {
		this.texts = new TEText[length];
	}
	
	public void setText(int index,TEText text) {
		this.texts[index] = text;
	}
	
	public TETrack[] getTracks() {
		return this.tracks;
	}
	
	public void setTracks(int length) {
		this.tracks = new TETrack[length];
	}
	
	public void setTrack(int index,TETrack track) {
		this.tracks[index] = track;
	}
	
	public TETimeSignature getTimeSignature() {
		return this.timeSignature;
	}
	
	public void setTimeSignature(TETimeSignature timeSignature) {
		this.timeSignature = timeSignature;
	}
	
	public TETempo getTempo() {
		return this.tempo;
	}
	
	public void setTempo(TETempo tempo) {
		this.tempo = tempo;
	}
	
	public int getStrings() {
		return this.strings;
	}
	
	public void setStrings(int strings) {
		this.strings = strings;
	}
	
	public int getMeasures() {
		return this.measures;
	}
	
	public void setMeasures(int measures) {
		this.measures = measures;
	}
	
	public List getComponents() {
		return this.components;
	}
	
	public void addTimeSignatureChange(TETimeSignatureChange tsChange){
		this.tsChanges.add(tsChange);
	}
	
	public TETimeSignature getTimeSignature(int measure){
		Iterator it = this.tsChanges.iterator();
		while(it.hasNext()){
			TETimeSignatureChange change = (TETimeSignatureChange)it.next();
			if(change.getMeasure() == measure){
				return change.getTimeSignature();
			}
		}
		return getTimeSignature();
	}
	
	public String toString(){
		String string = new String("[SONG] *** Tabledit file format ***\n");
		string +=  (this.getInfo().toString() + "\n");
		string +=  (this.getTempo().toString() + "\n");
		for(int i = 0; i < this.repeats.length; i ++){
			string +=  (this.repeats[i].toString() + "\n");
		}
		for(int i = 0; i < this.texts.length; i ++){
			string +=  (this.texts[i].toString() + "\n");
		}
		for(int i = 0; i < this.chords.length; i ++){
			string +=  (this.chords[i].toString() + "\n");
		}
		for(int i = 0; i < this.percussions.length; i ++){
			string +=  (this.percussions[i].toString() + "\n");
		}
		for(int i = 0; i < this.rhythms.length; i ++){
			string +=  (this.rhythms[i].toString() + "\n");
		}
		for(int i = 0; i < this.tracks.length; i ++){
			string +=  (this.tracks[i].toString() + "\n");
		}
		for(int i = 0; i < this.components.size(); i ++){
			string +=  (this.components.get(i).toString() + "\n");
		}
		return string;
	}
}
