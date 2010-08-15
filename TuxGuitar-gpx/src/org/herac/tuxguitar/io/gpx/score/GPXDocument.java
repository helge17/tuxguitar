package org.herac.tuxguitar.io.gpx.score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GPXDocument {
	
	private GPXScore score;
	private List tracks;
	private List masterBars;
	private List bars;
	private List voices;
	private List beats;
	private List notes;
	private List rhythms;
	private List automations;
	
	public GPXDocument(){
		this.score = new GPXScore();
		this.tracks = new ArrayList();
		this.masterBars = new ArrayList();
		this.bars = new ArrayList();
		this.voices = new ArrayList();
		this.beats = new ArrayList();
		this.notes = new ArrayList();
		this.rhythms = new ArrayList();
		this.automations = new ArrayList();
	}
	
	public GPXScore getScore(){
		return this.score;
	}
	
	public List getTracks() {
		return this.tracks;
	}
	
	public List getMasterBars() {
		return this.masterBars;
	}
	
	public List getBars() {
		return this.bars;
	}
	
	public List getVoices() {
		return this.voices;
	}
	
	public List getBeats() {
		return this.beats;
	}
	
	public List getNotes() {
		return this.notes;
	}
	
	public List getRhythms() {
		return this.rhythms;
	}
	
	public List getAutomations() {
		return this.automations;
	}
	
	public GPXBar getBar( int id ){
		Iterator it = this.bars.iterator();
		while( it.hasNext() ){
			GPXBar bar = (GPXBar)it.next();
			if( bar.getId() == id ){
				return bar;
			}
		}
		return null;
	}
	
	public GPXVoice getVoice( int id ){
		Iterator it = this.voices.iterator();
		while( it.hasNext() ){
			GPXVoice voice = (GPXVoice)it.next();
			if( voice.getId() == id ){
				return voice;
			}
		}
		return null;
	}
	
	public GPXBeat getBeat( int id ){
		Iterator it = this.beats.iterator();
		while( it.hasNext() ){
			GPXBeat beat = (GPXBeat)it.next();
			if( beat.getId() == id ){
				return beat;
			}
		}
		return null;
	}
	
	public GPXNote getNote( int id ){
		Iterator it = this.notes.iterator();
		while( it.hasNext() ){
			GPXNote note = (GPXNote)it.next();
			if( note.getId() == id ){
				return note;
			}
		}
		return null;
	}
	
	public GPXRhythm getRhythm( int id ){
		Iterator it = this.rhythms.iterator();
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
		
		Iterator it = this.automations.iterator();
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
