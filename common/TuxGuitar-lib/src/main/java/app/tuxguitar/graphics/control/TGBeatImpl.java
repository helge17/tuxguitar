package app.tuxguitar.graphics.control;

import java.util.Iterator;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.TGPickStroke;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;

public class TGBeatImpl extends TGBeat{
	/**
	 * desviacion a la izquierda
	 */
	public static final int JOINED_TYPE_NONE_LEFT = 1;
	/**
	 * desviacion a la derecha
	 */
	public static final int JOINED_TYPE_NONE_RIGHT = 2;
	/**
	 * Union a la izquierda
	 */
	public static final int JOINED_TYPE_LEFT = 3;
	/**
	 * Union a la derecha
	 */
	public static final int JOINED_TYPE_RIGHT = 4;

	private float posX;
	private float width;
	private TGNoteImpl maxNote;
	private TGNoteImpl minNote;
	private boolean[] usedStrings;
	private int joinedType;
	private boolean joinedGreaterThanQuarter;
	private TGBeatImpl join1;
	private TGBeatImpl join2;
	private TGBeatImpl previous;
	private TGBeatImpl next;
	private TGBeatGroup group;

	private TGBeatSpacing bs;
	private float effectWidth;

	private boolean accentuated;
	private boolean heavyAccentuated;
	private boolean harmonic;
	private boolean tapping;
	private boolean slapping;
	private boolean popping;
	private boolean palmMute;
	private boolean letRing;
	private boolean vibrato;
	private boolean trill;
	private boolean fadeIn;
	private boolean bend;

	public TGBeatImpl(TGFactory factory){
		super(factory);
	}

	public float getPosX() {
		return this.posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getMinimumWidth() {
		return this.width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setEffectWidth(float width) {
		this.effectWidth = width;
	}

	public TGNoteImpl getMinNote(){
		return this.minNote;
	}

	public TGNoteImpl getMaxNote(){
		return this.maxNote;
	}

	public TGMeasureImpl getMeasureImpl() {
		return (TGMeasureImpl)getMeasure();
	}

	public boolean[] getUsedStrings() {
		if(this.usedStrings == null){
			this.usedStrings = new boolean[getMeasure().getTrack().stringCount()];
		}
		return this.usedStrings;
	}

	public TGBeatImpl getJoin1() {
		return this.join1;
	}

	public void setJoin1(TGBeatImpl join1) {
		this.join1 = join1;
	}

	public TGBeatImpl getJoin2() {
		return this.join2;
	}

	public void setJoin2(TGBeatImpl join2) {
		this.join2 = join2;
	}

	public boolean isJoinedGreaterThanQuarter() {
		return this.joinedGreaterThanQuarter;
	}

	public boolean isBend() {
		return this.bend;
	}

	public void setJoinedGreaterThanQuarter(boolean joinedGreaterThanQuarter) {
		this.joinedGreaterThanQuarter = joinedGreaterThanQuarter;
	}

	public int getJoinedType() {
		return this.joinedType;
	}

	public void setJoinedType(int joinedType) {
		this.joinedType = joinedType;
	}

	public TGBeatImpl getPreviousBeat() {
		return this.previous;
	}

	public void setPreviousBeat(TGBeatImpl previous) {
		this.previous = previous;
	}

	public TGBeatImpl getNextBeat() {
		return this.next;
	}

	public void setNextBeat(TGBeatImpl next) {
		this.next = next;
	}

	public TGBeatGroup getBeatGroup() {
		return this.group;
	}

	public void setBeatGroup(TGBeatGroup group) {
		this.group = group;
	}

	public float getSpacing(TGLayout layout){
		return getMeasureImpl().getBeatSpacing(layout, this);
	}

	public boolean isPlaying(TGLayout layout){
		return layout.getComponent().isRunning(this);
	}

	public TGVoiceImpl getVoiceImpl(int index){
		TGVoice voice = super.getVoice(index);
		if(voice instanceof TGVoiceImpl){
			return (TGVoiceImpl)voice;
		}
		return null;
	}

	public void reset(){
		this.maxNote = null;
		this.minNote = null;
		this.usedStrings = new boolean[getMeasure().getTrack().stringCount()];
	}

	public void check( TGLayout layout , TGNoteImpl note){
		int value = TGNotation.computePosition(layout, note);
		if( this.maxNote == null || value > TGNotation.computePosition(layout, this.maxNote)){
			this.maxNote = note;
		}
		if( this.minNote == null || value < TGNotation.computePosition(layout, this.minNote)){
			this.minNote = note;
		}
		this.getUsedStrings();
		this.usedStrings[note.getString() - 1] = true;
	}

	public void resetEffectsSpacing( TGLayout layout ){
		this.bs = new TGBeatSpacing( layout );
		this.accentuated = false;
		this.heavyAccentuated = false;
		this.harmonic = false;
		this.tapping = false;
		this.slapping = false;
		this.popping = false;
		this.palmMute = false;
		this.letRing = false;
		this.fadeIn = false;
		this.vibrato = false;
		this.trill = false;
		this.bend = false;
	}

	public void updateEffectsSpacing(TGLayout layout,TGNoteEffect effect){
		if(effect.isAccentuatedNote()){
			this.accentuated = true;
		}
		if(effect.isHeavyAccentuatedNote()){
			this.heavyAccentuated = true;
		}
		if(effect.isHarmonic() && (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0 ){
			this.harmonic = true;
		}
		if(effect.isTapping()){
			this.tapping = true;
		}
		if(effect.isSlapping()){
			this.slapping = true;
		}
		if(effect.isPopping()){
			this.popping = true;
		}
		if(effect.isPalmMute()){
			this.palmMute = true;
		}
		if(effect.isLetRing()){
			this.letRing = true;
		}
		if(effect.isFadeIn()){
			this.fadeIn = true;
		}
		if(effect.isVibrato()){
			this.vibrato = true;
		}
		if(effect.isTrill()){
			this.trill = true;
		}
		if(effect.isBend()) {
			this.bend = true;
		}
	}

	public float getEffectsSpacing(TGLayout layout){
		if(this.accentuated){
			this.bs.setSize(TGBeatSpacing.POSITION_ACCENTUATED_EFFECT,layout.getEffectSpacing());
		}
		if(this.heavyAccentuated){
			this.bs.setSize(TGBeatSpacing.POSITION_HEAVY_ACCENTUATED_EFFECT,layout.getEffectSpacing());
		}
		if(this.harmonic){
			this.bs.setSize(TGBeatSpacing.POSITION_HARMONIC_EFFECT,layout.getEffectSpacing());
		}
		if(this.tapping){
			this.bs.setSize(TGBeatSpacing.POSITION_TAPPING_EFFECT,layout.getEffectSpacing());
		}
		if(this.slapping){
			this.bs.setSize(TGBeatSpacing.POSITION_SLAPPING_EFFECT,layout.getEffectSpacing());
		}
		if(this.popping){
			this.bs.setSize(TGBeatSpacing.POSITION_POPPING_EFFECT,layout.getEffectSpacing());
		}
		if(this.palmMute){
			this.bs.setSize(TGBeatSpacing.POSITION_PALM_MUTE_EFFECT,layout.getEffectSpacing());
		}
		if(this.letRing){
			this.bs.setSize(TGBeatSpacing.POSITION_LET_RING_EFFECT,layout.getEffectSpacing());
		}
		if(this.fadeIn){
			this.bs.setSize(TGBeatSpacing.POSITION_FADE_IN_EFFECT,layout.getEffectSpacing());
		}
		if(this.vibrato){
			this.bs.setSize(TGBeatSpacing.POSITION_VIBRATO_EFFECT,layout.getEffectSpacing());
		}
		if(this.trill){
			this.bs.setSize(TGBeatSpacing.POSITION_TRILL_EFFECT,layout.getEffectSpacing());
		}
		return this.bs.getSize();
	}

	public void registerBuffer(TGLayout layout) {
		Object chordRegistryKey = (TGChord.class.getName() + "-" + this.hashCode());

		TGResourceBuffer buffer = layout.getResourceBuffer();
		if( this.isChordBeat() ) {
			TGChordImpl tgChord = ((TGChordImpl) getChord());
			tgChord.registerBuffer(buffer, chordRegistryKey);
		} else if (buffer.isRegistered(chordRegistryKey) ) {
			buffer.unregister(chordRegistryKey);
		}
	}

	public void paint(TGLayout layout,UIPainter painter, float fromX, float fromY, boolean highlightPlayedBeat) {
		if(!layout.isPlayModeEnabled() && (layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 ){
			paintExtraLines(painter, layout,fromX, fromY);
		}
		for(int v = 0; v < TGBeat.MAX_VOICES; v ++){
			getVoiceImpl(v).paint(layout, painter, fromX, fromY);
		}
		if(!layout.isPlayModeEnabled()){
			if(isChordBeat()){
				TGChordImpl chord = (TGChordImpl)getChord();
				chord.paint(layout, painter, fromX, fromY);
			}
			if(getStroke().getDirection() != TGStroke.STROKE_NONE){
				paintStroke(layout, painter, fromX, fromY);
			}
			if(getPickStroke().getDirection() != TGPickStroke.PICK_STROKE_NONE){
				paintPickStroke(layout, painter, fromX, fromY);
			}
		}
		if(layout.isPlayModeEnabled() && isPlaying(layout) && highlightPlayedBeat){
			TGMeasureImpl measureImpl = getMeasureImpl();
			UIRectangle playedMeasure = measureImpl.getVerticalPosition(layout);
			float scale = layout.getScale();
			float leftSpacing = 8f* scale;
			float rightSpacing = 15f* scale;
			float width = this.effectWidth + leftSpacing + rightSpacing;
			int style = layout.getStyle();
			float yHighestNote = 0;
			float yLowestNote = 0;
			if ( (style & TGLayout.DISPLAY_SCORE) != 0) {
				for( int v = 0; v < MAX_VOICES; v ++){
					TGVoiceImpl voice = (TGVoiceImpl)getVoice(v);
					if(!voice.isEmpty()){
						Iterator<TGNote> it = voice.getNotes().iterator();
						while(it.hasNext()){
							TGNoteImpl note = (TGNoteImpl)it.next();
							yHighestNote = Math.min(note.getScorePosY(), yHighestNote);
							yLowestNote = Math.max(note.getScorePosY(), yLowestNote);
						}
					}
				}
			}
			float y1 = playedMeasure.getY();
			float y2 = y1 + playedMeasure.getHeight();
			// adapt to lowest note, only if tablature is not displayed
			if ((layout.getStyle() & TGLayout.DISPLAY_TABLATURE) == 0) {
				y2 = Math.max(y2, y1 + yLowestNote + 3*layout.getScoreLineSpacing());
			}
			// adapt to highest note, only if score is displayed
			if ((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0) {
				y1 += yHighestNote;
			}
			painter.setLineWidth(layout.getLineWidth(1));
			painter.initPath();
			painter.setAntialias(false);
			painter.addRoundedRectangle(fromX+getPosX()-leftSpacing+getSpacing(layout), y1, width , y2-y1, 3f*scale);
			painter.closePath();
		}
	}

	public void paintExtraLines(UIPainter painter,TGLayout layout,float fromX, float fromY){
		if(!isRestBeat()){
			float scoreY = (fromY + getMeasureImpl().getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
			paintExtraLines(painter,layout,getMinNote(), fromX, scoreY);
			paintExtraLines(painter,layout,getMaxNote(), fromX, scoreY);
		}
	}

	private void paintExtraLines(UIPainter painter,TGLayout layout,TGNoteImpl note,float fromX,float fromY){
		float scale = layout.getScale();
		float scoreLineSpacing = layout.getScoreLineSpacing();
		float spacing = (layout.getScoreLineSpacing() / 2f);
		float noteY = (fromY + note.getScorePosY() + spacing);
		float lineY = (fromY > noteY ? fromY : (fromY + (scoreLineSpacing * 4)));
		float x = fromX + getPosX() + getSpacing(layout);
		float x1 = x - (4 * scale);
		float x2 = x + (12 * scale);

		int direction = (fromY > noteY ? -1 : 1);
		int requiredExtraLines = this.findRequiredExtraLines(layout, lineY, noteY, direction);
		if( requiredExtraLines > 0 ) {
			layout.setLineStyle(painter);

			for(int i = 0; i < requiredExtraLines ; i ++) {
				painter.initPath();
				painter.setAntialias(false);
				painter.moveTo(x1, lineY + ((scoreLineSpacing * (i + 1)) * direction));
				painter.lineTo(x2, lineY + ((scoreLineSpacing * (i + 1)) * direction));
				painter.closePath();
			}
		}
	}

	private int findRequiredExtraLines(TGLayout layout, float lineY, float noteY, int direction) {
		int counter = 0;
		float spacing = (layout.getScoreLineSpacing() / 2f);
		float previousPosition = lineY;
		for(float position = (lineY + (spacing * direction));; position += (spacing * direction), counter ++) {
			float distance1 = Math.abs(previousPosition - noteY);
			float distance2 = Math.abs(position - noteY);

			if( Math.min(distance1, distance2) == distance1) {
				return (counter > 0 ? counter / 2 : 0);
			}
			previousPosition = position;
		}
	}

	public void paintStroke(TGLayout layout, UIPainter painter, float fromX, float fromY){
		int style = layout.getStyle();
		float scale = layout.getScale();
		float x = (fromX + getPosX() + getSpacing(layout) + ( 12f * scale ));
		float y1 = 0;
		float y2 = 0;

		if((style & TGLayout.DISPLAY_TABLATURE) != 0) {
			float height = getMeasureImpl().getTrackImpl().getTabHeight();
			float margin = (Math.min((layout.getStringSpacing() * 2f), height - (layout.getStringSpacing() * 2f)) / 2f);
			float y = (fromY + getPaintPosition(TGTrackSpacing.POSITION_TABLATURE));
			y1 = (y + margin);
			y2 = (y + height - margin);
		}
		else if((style & TGLayout.DISPLAY_SCORE) != 0) {
			float y = (fromY + getPaintPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
			y1 = (y + layout.getScoreLineSpacing());
			y2 = (y + (getMeasureImpl().getTrackImpl().getScoreHeight() - layout.getScoreLineSpacing()));
		}

		if( getStroke().getDirection() == TGStroke.STROKE_UP ) {
			painter.setLineWidth(layout.getLineWidth(0));
			painter.initPath();
			painter.moveTo( x, y1 );
			painter.lineTo( x, y2 );
			painter.moveTo( x - (2.0f * scale), y2 - (5.0f * scale));
			painter.lineTo( x, y2 );
			painter.moveTo( x + (2.0f * scale), y2 - (5.0f * scale));
			painter.lineTo( x, y2 );
			painter.closePath();
		} else if( getStroke().getDirection() == TGStroke.STROKE_DOWN ) {
			painter.setLineWidth(layout.getLineWidth(0));
			painter.initPath();
			painter.moveTo( x, y2 );
			painter.lineTo( x, y1 );
			painter.moveTo( x - (2.0f * scale), y1 + (5.0f * scale));
			painter.lineTo( x, y1 );
			painter.moveTo( x + (2.0f * scale), y1 + (5.0f * scale));
			painter.lineTo( x, y1 );
			painter.closePath();
		}
	}

	public void paintPickStroke(TGLayout layout, UIPainter painter, float fromX, float fromY){
		float scale = layout.getScale();
		float x = (fromX + getPosX() + getSpacing(layout));
		float y = (fromY + getPaintPosition(TGTrackSpacing.POSITION_PICK_STROKES));

		if( getPickStroke().getDirection() == TGPickStroke.PICK_STROKE_UP ) {
			painter.setLineWidth(layout.getLineWidth(0));
			painter.initPath();
			painter.moveTo( x - 3.0f * scale, y );
			painter.lineTo( x, y + 8.0f * scale );
			painter.moveTo( x + 3.0f * scale, y );
			painter.lineTo( x, y + 8.0f * scale );
			painter.closePath();
		} else if( getPickStroke().getDirection() == TGPickStroke.PICK_STROKE_DOWN ) {
			painter.setLineWidth(layout.getLineWidth(0));
			painter.initPath();
			painter.moveTo( x - 3.0f * scale, y + 6.0f * scale);
			painter.lineTo( x - 3.0f * scale, y );
			painter.moveTo( x + 3.0f * scale, y );
			painter.lineTo( x + 3.0f * scale, y + 6.0f * scale);
			painter.closePath();
			painter.setLineWidth(layout.getLineWidth(2));
			painter.initPath();
			painter.moveTo( x - 3.0f * scale, y );
			painter.lineTo( x + 3.0f * scale, y );
			painter.closePath();
		}
	}

	public float getPaintPosition(int index){
		return getMeasureImpl().getTs().getPosition(index);
	}

	public TGBeatSpacing getBs(){
		return this.bs;
	}
}
