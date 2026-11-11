package app.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.song.models.TGVoice;

public class TGBeatGroup {

	public static final int DIRECTION_NOT_SET = 0;
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
		this.direction = DIRECTION_NOT_SET;
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
		if( this.direction == DIRECTION_NOT_SET ){
			if (measure.getNotEmptyVoices() > 1 ){
				this.direction = this.voice == 0 ? DIRECTION_UP : DIRECTION_DOWN;
			}else if ( (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0 ){
				this.direction = DIRECTION_DOWN;
			}else{
				int minPosition = TGNotation.computePosition(layout, this.minNote);
				int maxPosition = TGNotation.computePosition(layout, this.maxNote);
				int middlePosition = (maxPosition > minPosition ? (minPosition + ((maxPosition - minPosition) / 2)) : minPosition);
				if( middlePosition > 3 ){
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

	public float getY1(TGLayout layout, TGNoteImpl note){
		float scale = (layout.getScoreLineSpacing() / 2f);
		float scoreLineY = (TGNotation.computePosition(layout, note) * scale);

		return scoreLineY;
	}

	// the end of the stem at x: on the beam, below the lowest notes for stems down, above the highest for stems up
	public float getY2(TGLayout layout, float x){
		if(this.direction == DIRECTION_DOWN){
			return getBeamY(layout, x, this.maxNote, this.firstMaxNote, this.lastMaxNote, getDownOffset(layout));
		}
		return getBeamY(layout, x, this.minNote, this.firstMinNote, this.lastMinNote, -getUpOffset(layout));
	}

	// flat at the outermost note when it is inside the group, else slanted from the first to the last note
	private float getBeamY(TGLayout layout, float x, TGNoteImpl outermost, TGNoteImpl first, TGNoteImpl last, float offset){
		if( outermost != first && outermost != last ){
			return (getY1(layout, outermost) + offset);
		}
		float y1 = (getY1(layout, first) + offset);
		float y2 = (getY1(layout, last) + offset);
		// the slope is limited: the end nearer the notes moves toward the end further out
		float maxDistance = (10f * layout.getScale());
		float limit = (this.direction == DIRECTION_DOWN ? Math.max(y1, y2) - maxDistance : Math.min(y1, y2) + maxDistance);
		return getLineY(getStemX(layout, first), limitTo(y1, limit), getStemX(layout, last), limitTo(y2, limit), x);
	}

	private float limitTo(float y, float limit){
		return (this.direction == DIRECTION_DOWN ? Math.max(y, limit) : Math.min(y, limit));
	}

	private static float getStemX(TGLayout layout, TGNoteImpl note){
		return note.getPosX() + note.getBeatImpl().getSpacing(layout);
	}

	// y at x on the line through (x1, y1) and (x2, y2); y1 when that line is vertical
	private static float getLineY(float x1, float y1, float x2, float y2, float x){
		float y = 0;
		if( (y1 - y2) != 0 && (x1 - x2) != 0 && (x1 - x) != 0 ){
			y = (((y1 - y2) / (x1 - x2)) * (x1 - x));
		}
		return y1 - y;
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
