package org.herac.tuxguitar.io.gpx.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GPXDocument {
	
	private GPXScore score;
	private List<GPXTrack> tracks;
	private List<GPXMasterBar> masterBars;
	private List<GPXBar> bars;
	private List<GPXVoice> voices;
	private List<GPXBeat> beats;
	private List<GPXNote> notes;
	private List<GPXRhythm> rhythms;
	private List<GPXAutomation> automations;
	
	public GPXDocument(){
		this.score = new GPXScore();
		this.tracks = new ArrayList<GPXTrack>();
		this.masterBars = new ArrayList<GPXMasterBar>();
		this.bars = new ArrayList<GPXBar>();
		this.voices = new ArrayList<GPXVoice>();
		this.beats = new ArrayList<GPXBeat>();
		this.notes = new ArrayList<GPXNote>();
		this.rhythms = new ArrayList<GPXRhythm>();
		this.automations = new ArrayList<GPXAutomation>();
	}
	
	public GPXScore getScore(){
		return this.score;
	}
	
	public List<GPXTrack> getTracks() {
		return this.tracks;
	}
	
	public List<GPXMasterBar> getMasterBars() {
		return this.masterBars;
	}
	
	public List<GPXBar> getBars() {
		return this.bars;
	}
	
	public List<GPXVoice> getVoices() {
		return this.voices;
	}
	
	public List<GPXBeat> getBeats() {
		return this.beats;
	}
	
	public List<GPXNote> getNotes() {
		return this.notes;
	}
	
	public List<GPXRhythm> getRhythms() {
		return this.rhythms;
	}
	
	public List<GPXAutomation> getAutomations() {
		return this.automations;
	}
	
	public GPXBar getBar( int id ){
		Iterator<GPXBar> it = this.bars.iterator();
		while( it.hasNext() ){
			GPXBar bar = (GPXBar)it.next();
			if( bar.getId() == id ){
				return bar;
			}
		}
		return null;
	}
	
	public GPXVoice getVoice( int id ){
		Iterator<GPXVoice> it = this.voices.iterator();
		while( it.hasNext() ){
			GPXVoice voice = (GPXVoice)it.next();
			if( voice.getId() == id ){
				return voice;
			}
		}
		return null;
	}
	
	public GPXBeat getBeat( int id ){
		Iterator<GPXBeat> it = this.beats.iterator();
		while( it.hasNext() ){
			GPXBeat beat = (GPXBeat)it.next();
			if( beat.getId() == id ){
				return beat;
			}
		}
		return null;
	}
	
	public GPXNote getNote( int id ){
		Iterator<GPXNote> it = this.notes.iterator();
		while( it.hasNext() ){
			GPXNote note = (GPXNote)it.next();
			if( note.getId() == id ){
				return note;
			}
		}
		return null;
	}
	
	public GPXRhythm getRhythm( int id ){
		Iterator<GPXRhythm> it = this.rhythms.iterator();
		while( it.hasNext() ){
			GPXRhythm rhythm = (GPXRhythm)it.next();
			if( rhythm.getId() == id ){
				return rhythm;
			}
		}
		return null;
	}
	
	public GPXAutomation getAutomation( String type, int untilBarId ){
		GPXAutomation result = null;
		
		Iterator<GPXAutomation> it = this.automations.iterator();
		while( it.hasNext() ){
			GPXAutomation automation = (GPXAutomation)it.next();
			if( automation.getType() != null && automation.getType().equals( type ) ){
				if( automation.getBarId() <= untilBarId  && ( result == null || automation.getBarId() > result.getBarId() )){
					result = automation;
				}
			}
		}
		return result;
	}
}
