/*
 * Created on 23-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGTrack {
	public static final int MAX_OFFSET = 24;
	public static final int MIN_OFFSET = -24;
	
	private int number;
	private int offset;
	private int channelId;
	private boolean solo;
	private boolean mute;
	private String name;
	private List<TGMeasure> measures;
	private List<TGString> strings;
	private TGColor color;
	private TGLyric lyrics;
	private TGSong song;
	
	public TGTrack(TGFactory factory) {
		this.number = 0;
		this.offset = 0;
		this.channelId = -1;
		this.solo = false;
		this.mute = false;
		this.name = new String();
		this.measures = new ArrayList<TGMeasure>();
		this.strings = new ArrayList<TGString>();
		this.color = factory.newColor();
		this.lyrics = factory.newLyric();
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public Iterator<TGMeasure> getMeasures() {
		return this.measures.iterator();
	}
	
	public void addMeasure(TGMeasure measure){
		measure.setTrack(this);
		this.measures.add(measure);
	}
	
	public void addMeasure(int index,TGMeasure measure){
		measure.setTrack(this);
		this.measures.add(index,measure);
	}
	
	public TGMeasure getMeasure(int index){
		if(index >= 0 && index < countMeasures()){
			return this.measures.get(index);
		}
		return null;
	}
	
	public void removeMeasure(int index){
		this.measures.remove(index);
	}
	
	public int countMeasures(){
		return this.measures.size();
	}
	
	public List<TGString> getStrings() {
		return this.strings;
	}
	
	public void setStrings(List<TGString> strings) {
		this.strings = strings;
	}
	
	public TGColor getColor() {
		return this.color;
	}
	
	public void setColor(TGColor color) {
		this.color = color;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getOffset() {
		return this.offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public boolean isSolo() {
		return this.solo;
	}
	
	public void setSolo(boolean solo) {
		this.solo = solo;
	}
	
	public boolean isMute() {
		return this.mute;
	}
	
	public void setMute(boolean mute) {
		this.mute = mute;
	}
	
	public int getChannelId() {
		return this.channelId;
	}
	
	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}
	
	public TGLyric getLyrics() {
		return this.lyrics;
	}
	
	public void setLyrics(TGLyric lyrics) {
		this.lyrics = lyrics;
	}
	
	public TGString getString(int number){
		return this.strings.get(number - 1);
	}
	
	public int stringCount(){
		return this.strings.size();
	}
	
	public TGSong getSong() {
		return this.song;
	}
	
	public void setSong(TGSong song) {
		this.song = song;
	}
	
	public void clear(){
		int measureCount = this.countMeasures();
		for(int i = 0 ; i < measureCount ; i ++) {
			TGMeasure tgMeasure = this.getMeasure(i);
			tgMeasure.clear();
		}
		
		this.strings.clear();
		this.measures.clear();
	}
	
	public TGTrack clone(TGFactory factory,TGSong song){
		TGTrack tgTrack = factory.newTrack();
		tgTrack.copyFrom(factory, song, this);
		return tgTrack;
	}
	
	public void copyFrom(TGFactory factory, TGSong song ,TGTrack track){
		this.clear();
		this.setNumber(track.getNumber());
		this.setName(track.getName());
		this.setOffset(track.getOffset());
		this.setSolo(track.isSolo());
		this.setMute(track.isMute());
		this.setChannelId(track.getChannelId());
		this.getColor().copyFrom(track.getColor());
		this.getLyrics().copyFrom(track.getLyrics());
		for (int i = 0; i < track.getStrings().size(); i++) {
			TGString string = track.getStrings().get(i);
			this.getStrings().add(string.clone(factory));
		}
		for (int i = 0; i < track.countMeasures(); i++) {
			TGMeasure measure = track.getMeasure(i);
			this.addMeasure(measure.clone(factory, song.getMeasureHeader(i)));
		}
	}
}
