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
	private boolean solo;
	private boolean mute;
	private String name;
	private List measures;
	private List strings;
	private TGChannel channel;
	private TGColor color;
	private TGLyric lyrics;
	private TGSong song;
	
	public TGTrack(TGFactory factory) {
		this.number = 0;
		this.offset = 0;
		this.solo = false;
		this.mute = false;
		this.name = new String();
		this.measures = new ArrayList();
		this.strings = new ArrayList();
		this.channel = factory.newChannel();
		this.color = factory.newColor();
		this.lyrics = factory.newLyric();
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public Iterator getMeasures() {
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
			return (TGMeasure)this.measures.get(index);
		}
		return null;
	}
	
	public void removeMeasure(int index){
		this.measures.remove(index);
	}
	
	public int countMeasures(){
		return this.measures.size();
	}
	
	public TGChannel getChannel() {
		return this.channel;
	}
	
	public void setChannel(TGChannel channel) {
		this.channel = channel;
	}
	
	public List getStrings() {
		return this.strings;
	}
	
	public void setStrings(List strings) {
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
	
	public TGLyric getLyrics() {
		return this.lyrics;
	}
	
	public void setLyrics(TGLyric lyrics) {
		this.lyrics = lyrics;
	}
	
	public TGString getString(int number){
		return (TGString)this.strings.get(number - 1);
	}
	
	public int stringCount(){
		return this.strings.size();
	}
	
	public boolean isPercussionTrack(){
		return (getChannel().isPercussionChannel());
	}
	
	public TGSong getSong() {
		return this.song;
	}
	
	public void setSong(TGSong song) {
		this.song = song;
	}
	
	public void clear(){
		this.strings.clear();
		this.measures.clear();
	}
	
	public TGTrack clone(TGFactory factory,TGSong song){
		TGTrack track = factory.newTrack();
		copy(factory, song, track);
		return track;
	}
	
	public void copy(TGFactory factory,TGSong song,TGTrack track){
		track.clear();
		track.setNumber(getNumber());
		track.setName(getName());
		track.setOffset(getOffset());
		getChannel().copy(track.getChannel());
		getColor().copy(track.getColor());
		getLyrics().copy(track.getLyrics());
		for (int i = 0; i < getStrings().size(); i++) {
			TGString string = (TGString) getStrings().get(i);
			track.getStrings().add(string.clone(factory));
		}
		for (int i = 0; i < countMeasures(); i++) {
			TGMeasure measure = getMeasure(i);
			track.addMeasure(measure.clone(factory,song.getMeasureHeader(i)));
		}
	}
	
}
