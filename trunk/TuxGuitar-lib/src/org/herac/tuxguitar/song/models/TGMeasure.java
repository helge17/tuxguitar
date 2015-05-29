/*
 * Created on 26-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGMeasure {
	
	public static final int CLEF_TREBLE = 1;
	public static final int CLEF_BASS = 2;
	public static final int CLEF_TENOR = 3;
	public static final int CLEF_ALTO = 4;
	
	public static final int DEFAULT_CLEF = CLEF_TREBLE;
	public static final int DEFAULT_KEY_SIGNATURE= 0;
	
	private TGMeasureHeader header;
	private TGTrack track;
	private int clef;
	private int keySignature;
	
	private List<TGBeat> beats;
	
	public TGMeasure(TGMeasureHeader header){
		this.header = header;
		this.clef = DEFAULT_CLEF;
		this.keySignature = DEFAULT_KEY_SIGNATURE;
		this.beats = new ArrayList<TGBeat>();
	}
	
	public TGTrack getTrack() {
		return this.track;
	}
	
	public void setTrack(TGTrack track) {
		this.track = track;
	}
	
	public int getClef() {
		return this.clef;
	}
	
	public void setClef(int clef) {
		this.clef = clef;
	}
	
	public int getKeySignature() {
		return this.keySignature;
	}
	
	public void setKeySignature(int keySignature) {
		this.keySignature = keySignature;
	}
	
	public List<TGBeat> getBeats() {
		return this.beats;
	}
	
	public void addBeat(TGBeat beat){
		beat.setMeasure(this);
		this.beats.add(beat);
	}
	
	public void moveBeat(int index,TGBeat beat){
		this.beats.remove(beat);
		this.beats.add(index,beat);
	}
	
	public void removeBeat(TGBeat beat){
		this.beats.remove(beat);
	}
	
	public TGBeat getBeat(int index){
		if(index >= 0 && index < countBeats()){
			return this.beats.get(index);
		}
		return null;
	}
	
	public int countBeats(){
		return this.beats.size();
	}
	
	public TGMeasureHeader getHeader() {
		return this.header;
	}
	
	public void setHeader(TGMeasureHeader header) {
		this.header = header;
	}
	
	public int getNumber() {
		return this.header.getNumber();
	}
	
	public int getRepeatClose() {
		return this.header.getRepeatClose();
	}
	
	public long getStart() {
		return this.header.getStart();
	}
	
	public TGTempo getTempo() {
		return this.header.getTempo();
	}
	
	public TGTimeSignature getTimeSignature() {
		return this.header.getTimeSignature();
	}
	
	public boolean isRepeatOpen() {
		return this.header.isRepeatOpen();
	}
	
	public int getTripletFeel() {
		return this.header.getTripletFeel();
	}
	
	public long getLength() {
		return this.header.getLength();
	}
	
	public TGMarker getMarker(){
		return this.header.getMarker();
	}
	
	public boolean hasMarker() {
		return this.header.hasMarker();
	}
	
	public void clear(){
		this.beats.clear();
	}
	
	public void copyFrom(TGFactory factory, TGMeasure measure){
		this.clef = measure.getClef();
		this.keySignature = measure.getKeySignature();
		this.beats.clear();
		for(int i = 0; i < measure.countBeats(); i ++){
			this.addBeat(measure.getBeat(i).clone(factory));
		}
	}
	
	public TGMeasure clone(TGFactory factory,TGMeasureHeader header){
		TGMeasure tgMeasure = factory.newMeasure(header);
		tgMeasure.copyFrom(factory, this);
		return tgMeasure;
	}
}
