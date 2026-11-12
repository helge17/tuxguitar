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

	// which side of the current pick a note must be on to replace it: in time, then in score position
	private static final int EARLIER = -1;
	private static final int LATER = 1;
	private static final int ANYTIME = 0;
	private static final int SMALLER = -1;
	private static final int GREATER = 1;

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

		this.firstMinNote = pick(layout, this.firstMinNote, note, value, EARLIER, SMALLER);
		this.firstMaxNote = pick(layout, this.firstMaxNote, note, value, EARLIER, GREATER);
		this.lastMinNote = pick(layout, this.lastMinNote, note, value, LATER, SMALLER);
		this.lastMaxNote = pick(layout, this.lastMaxNote, note, value, LATER, GREATER);
		this.maxNote = pick(layout, this.maxNote, note, value, ANYTIME, GREATER);
		this.minNote = pick(layout, this.minNote, note, value, ANYTIME, SMALLER);
	}

	// the note (at position value) replaces the current pick when there is none yet, when it starts on the wanted
	// side of it, or when it starts with it and its position is on the wanted side; ties keep the current pick
	private static TGNoteImpl pick(TGLayout layout, TGNoteImpl current, TGNoteImpl note, int value, int startSide, int positionSide){
		if( current == null ){
			return note;
		}
		int side = (Long.compare(getStart(note), getStart(current)) * startSide);
		if( side == 0 ){
			side = (Integer.compare(value, TGNotation.computePosition(layout, current)) * positionSide);
		}
		return (side > 0 ? note : current);
	}

	private static long getStart(TGNoteImpl note){
		return note.getVoice().getBeat().getStart();
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
