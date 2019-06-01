package org.herac.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.song.models.TGVoice;

public class TGBeatGroup {
	
	public static final int DIRECTION_NOT_SETTED = 0;
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = 2;
	
	private static final int UP_OFFSET = 28;
	private static final int DOWN_OFFSET = 35;
	
	private int voice;
	private int direction;
	private List<TGVoice> voices;
	private TGNoteImpl firstMinNote;
	private TGNoteImpl firstMaxNote;
	private TGNoteImpl lastMinNote;
	private TGNoteImpl lastMaxNote;
	private TGNoteImpl maxNote;
	private TGNoteImpl minNote;
	
	public TGBeatGroup(int voice){
		this.voice = voice;
		this.voices = new ArrayList<TGVoice>();
		this.direction = DIRECTION_NOT_SETTED;
		this.firstMinNote = null;
		this.firstMaxNote = null;
		this.lastMinNote = null;
		this.lastMaxNote = null;
		this.maxNote = null;
		this.minNote = null;
	}
	
	public void check(TGLayout layout, TGVoiceImpl voice){
		this.check(layout, voice.getMaxNote());
		this.check(layout, voice.getMinNote());
		this.voices.add( voice );
		if( voice.getDirection() != TGVoice.DIRECTION_NONE ){
			if( voice.getDirection() == TGVoice.DIRECTION_UP ){
				this.direction = DIRECTION_UP;
			}
			else if( voice.getDirection() == TGVoice.DIRECTION_DOWN ){
				this.direction = DIRECTION_DOWN;
			}
		}
	}
	
	private void check(TGLayout layout, TGNoteImpl note){
		int value = TGNotation.computePosition(layout, note);
		
		//FIRST MIN NOTE
		if( this.firstMinNote == null || note.getVoice().getBeat().getStart() < this.firstMinNote.getVoice().getBeat().getStart()){
			this.firstMinNote = note;
		}else if(note.getVoice().getBeat().getStart() == this.firstMinNote.getVoice().getBeat().getStart()){
			if( value < TGNotation.computePosition(layout, this.firstMinNote)){
				this.firstMinNote = note;
			}
		}
		//FIRST MAX NOTE
		if( this.firstMaxNote == null || note.getVoice().getBeat().getStart() < this.firstMaxNote.getVoice().getBeat().getStart()){
			this.firstMaxNote = note;
		}else if(note.getVoice().getBeat().getStart() == this.firstMaxNote.getVoice().getBeat().getStart()){
			if( value > TGNotation.computePosition(layout, this.firstMaxNote)){
				this.firstMaxNote = note;
			}
		}
		
		//LAST MIN NOTE
		if( this.lastMinNote == null || note.getVoice().getBeat().getStart() > this.lastMinNote.getVoice().getBeat().getStart()){
			this.lastMinNote = note;
		}else if(note.getVoice().getBeat().getStart() == this.lastMinNote.getVoice().getBeat().getStart()){
			if( value < TGNotation.computePosition(layout, this.lastMinNote)){
				this.lastMinNote = note;
			}
		}
		//LAST MIN NOTE
		if( this.lastMaxNote == null || note.getVoice().getBeat().getStart() > this.lastMaxNote.getVoice().getBeat().getStart()){
			this.lastMaxNote = note;
		}else if(note.getVoice().getBeat().getStart() == this.lastMaxNote.getVoice().getBeat().getStart()){
			if( value > TGNotation.computePosition(layout, this.lastMaxNote)){
				this.lastMaxNote = note;
			}
		}
		
		if( this.maxNote == null || value > TGNotation.computePosition(layout, this.maxNote)){
			this.maxNote = note;
		}
		if( this.minNote == null || value < TGNotation.computePosition(layout, this.minNote)){
			this.minNote = note;
		}
	}
	
	public void finish(TGLayout layout, TGMeasureImpl measure){
		if( this.direction == DIRECTION_NOT_SETTED ){
			if (measure.getNotEmptyVoices() > 1 ){
				this.direction = this.voice == 0 ? DIRECTION_UP : DIRECTION_DOWN;
			}else if ( (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0 ){
				this.direction = DIRECTION_DOWN;
			}else{
				int max = Math.abs(TGNotation.computePosition(layout, this.maxNote) + 3);
				int min = Math.abs(TGNotation.computePosition(layout, this.minNote) - 3);
				if( max > min){
					this.direction = DIRECTION_UP;
				}else{
					this.direction = DIRECTION_DOWN;
				}
			}
		}
	}
	
	public List<TGVoice> getVoices(){
		return this.voices;
	}
	
	public float getY1(TGLayout layout, TGNoteImpl note, int key, int clef){
		float scale = (layout.getScoreLineSpacing() / 2f);
		float scoreLineY = (TGNotation.computePosition(layout, note) * scale);
		
		return scoreLineY;
	}
	
	public float getY2(TGLayout layout, float x, int key, int clef){
		float maxDistance = (10f * layout.getScale());
		float upOffset = TGBeatGroup.getUpOffset(layout);
		float downOffset = TGBeatGroup.getDownOffset(layout);
		if(this.direction == DIRECTION_DOWN){
			if( this.maxNote != this.firstMaxNote && this.maxNote != this.lastMaxNote ){
				return (getY1(layout, this.maxNote, key, clef) + downOffset);
			}
			
			float y = 0;
			float x1 = this.firstMaxNote.getPosX() + this.firstMaxNote.getBeatImpl().getSpacing(layout);
			float x2 = this.lastMaxNote.getPosX() + this.lastMaxNote.getBeatImpl().getSpacing(layout);
			float y1 =  (getY1(layout, this.firstMaxNote, key, clef) +  downOffset);
			float y2 =  (getY1(layout, this.lastMaxNote, key, clef) +  downOffset);
			
			if( y1 > y2 && (y1 - y2) > maxDistance ) {
				y2 = (y1 - maxDistance);
			}
			if( y2 > y1 && (y2 - y1) > maxDistance ) {
				y1 = (y2 - maxDistance);
			}
			
			if( (y1 - y2) != 0 && (x1 - x2) != 0 && (x1 - x) != 0 ){
				y = (((y1 -y2) / (x1 - x2)) * (x1 - x));
			}
			return y1 - y;
		}else if(this.minNote != this.firstMinNote && this.minNote != this.lastMinNote){
			return (getY1(layout, this.minNote, key, clef) - upOffset);
		}else{
			float y = 0;
			float x1 = this.firstMinNote.getPosX() + this.firstMinNote.getBeatImpl().getSpacing(layout);
			float x2 = this.lastMinNote.getPosX() + this.lastMinNote.getBeatImpl().getSpacing(layout);
			float y1 = (getY1(layout,this.firstMinNote, key, clef) - upOffset);
			float y2 = (getY1(layout,this.lastMinNote, key, clef) - upOffset);
			
			if( y1 < y2 && (y2 - y1) > maxDistance ) {
				y2 = (y1 + maxDistance);
			}
			if( y2 < y1 && (y1 - y2) > maxDistance ) {
				y1 = (y2 + maxDistance);
			}
			
			if( (y1 - y2) != 0 && (x1 - x2) != 0 && (x1 - x) != 0 ){
				y = (((y1 - y2) / (x1 - x2)) * (x1 - x));
			}
			return y1 - y;
		}
	}
	
	public int getDirection() {
		return this.direction;
	}
	
	public TGNoteImpl getMinNote(){
		return this.minNote;
	}
	
	public TGNoteImpl getMaxNote(){
		return this.maxNote;
	}
	
	public static float getUpOffset(TGLayout layout){
		float scale = (layout.getScoreLineSpacing() / 8.0f);
		return (UP_OFFSET * scale);
	}
	
	public static float getDownOffset(TGLayout layout){
		float scale = (layout.getScoreLineSpacing() / 8.0f);
		return (DOWN_OFFSET * scale);
	}
}
