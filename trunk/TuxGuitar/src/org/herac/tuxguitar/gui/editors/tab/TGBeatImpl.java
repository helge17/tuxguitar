package org.herac.tuxguitar.gui.editors.tab;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGVoice;

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
	
	private int posX;
	private int width;
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
	
	private boolean accentuated;
	private boolean heavyAccentuated;
	private boolean harmonic;
	private boolean tapping;
	private boolean slapping;
	private boolean popping;
	private boolean palmMute;
	private boolean vibrato;
	private boolean trill;
	private boolean fadeIn;
	
	public TGBeatImpl(TGFactory factory){
		super(factory);
	}
	
	public int getPosX() {
		return this.posX;
	}
	
	public void setPosX(int posX) {
		this.posX = posX;
	}
	
	public int getMinimumWidth() {
		return this.width;
	}
	
	public void setWidth(int width) {
		this.width = width;
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
	
	public int getSpacing(){
		return getMeasureImpl().getBeatSpacing(this);
	}
	
	public boolean isPlaying(ViewLayout layout){
		return (getMeasureImpl().isPlaying(layout) && TuxGuitar.instance().getEditorCache().isPlaying(getMeasure(),this));
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
	
	public void check( ViewLayout layout , TGNoteImpl note){
		int value = note.getRealValue();
		if(this.maxNote == null || value > this.maxNote.getRealValue()){
			this.maxNote = note;
		}
		if(this.minNote == null || value < this.minNote.getRealValue()){
			this.minNote = note;
		}
		this.getUsedStrings();
		this.usedStrings[note.getString() - 1] = true;
	}
	
	public void resetEffectsSpacing( ViewLayout layout ){
		this.bs = new TGBeatSpacing( layout );
		this.accentuated = false;
		this.heavyAccentuated = false;
		this.harmonic = false;
		this.tapping = false;
		this.slapping = false;
		this.popping = false;
		this.palmMute = false;
		this.fadeIn = false;
		this.vibrato = false;
		this.trill = false;
	}
	
	public void updateEffectsSpacing(ViewLayout layout,TGNoteEffect effect){
		if(effect.isAccentuatedNote()){
			this.accentuated = true;
		}
		if(effect.isHeavyAccentuatedNote()){
			this.heavyAccentuated = true;
		}
		if(effect.isHarmonic() && (layout.getStyle() & ViewLayout.DISPLAY_SCORE) == 0 ){
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
		if(effect.isFadeIn()){
			this.fadeIn = true;
		}
		if(effect.isVibrato()){
			this.vibrato = true;
		}
		if(effect.isTrill()){
			this.trill = true;
		}
	}
	
	public int getEffectsSpacing(ViewLayout layout){
		if(this.accentuated){
			this.bs.setSize(TGBeatSpacing.POSITION_ACCENTUATED_EFFECT,layout.getEffectSpacing());
		}
		if(this.heavyAccentuated){
			this.bs.setSize(TGBeatSpacing.POSITION_HEAVY_ACCENTUATED_EFFECT,layout.getEffectSpacing());
		}
		if(this.harmonic){
			this.bs.setSize(TGBeatSpacing.POSITION_HARMONIC_EFFEC,layout.getEffectSpacing());
		}
		if(this.tapping){
			this.bs.setSize(TGBeatSpacing.POSITION_TAPPING_EFFEC,layout.getEffectSpacing());
		}
		if(this.slapping){
			this.bs.setSize(TGBeatSpacing.POSITION_SLAPPING_EFFEC,layout.getEffectSpacing());
		}
		if(this.popping){
			this.bs.setSize(TGBeatSpacing.POSITION_POPPING_EFFEC,layout.getEffectSpacing());
		}
		if(this.palmMute){
			this.bs.setSize(TGBeatSpacing.POSITION_PALM_MUTE_EFFEC,layout.getEffectSpacing());
		}
		if(this.fadeIn){
			this.bs.setSize(TGBeatSpacing.POSITION_FADE_IN,layout.getEffectSpacing());
		}
		if(this.vibrato){
			this.bs.setSize(TGBeatSpacing.POSITION_VIBRATO_EFFEC,layout.getEffectSpacing());
		}
		if(this.trill){
			this.bs.setSize(TGBeatSpacing.POSITION_TRILL_EFFEC,layout.getEffectSpacing());
		}
		return this.bs.getSize();
	}
	
	public void play(){
		if(!TuxGuitar.instance().getPlayer().isRunning()){
			new Thread(new Runnable() {
				public void run() {
					List notes = new ArrayList();
					for( int v = 0; v < countVoices(); v ++){
						notes.addAll( getVoice(v).getNotes() );
					}
					TuxGuitar.instance().getPlayer().playBeat(getMeasure().getTrack(),notes);
				}
			}).start();
		}
	}
	
	public void paint(ViewLayout layout,TGPainter painter, int fromX, int fromY/*,boolean playMode*/) {
		if(!layout.isPlayModeEnabled() && (layout.getStyle() & ViewLayout.DISPLAY_SCORE) != 0 ){
			paintExtraLines(painter, layout,fromX, fromY);
		}
		for(int v = 0; v < TGBeat.MAX_VOICES; v ++){
			getVoiceImpl(v).paint(layout, painter, fromX, fromY);
		}
		if(!layout.isPlayModeEnabled()){
			if(isChordBeat()){
				TGChordImpl chord = (TGChordImpl)getChord();
				chord.paint(layout,painter,fromX,fromY);
			}
			if(getStroke().getDirection() != TGStroke.STROKE_NONE){
				paintStroke(layout, painter, fromX, fromY);
			}
		}
	}
	
	public void paintExtraLines(TGPainter painter,ViewLayout layout,int fromX, int fromY){
		if(!isRestBeat()){
			int scoreY = (fromY + getMeasureImpl().getTs().getPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES));
			paintExtraLines(painter,layout,getMinNote(), fromX, scoreY);
			paintExtraLines(painter,layout,getMaxNote(), fromX, scoreY);
		}
	}
	
	private void paintExtraLines(TGPainter painter,ViewLayout layout,TGNoteImpl note,int fromX,int fromY){
		float scale = layout.getScale();
		int y = fromY + note.getScorePosY();
		int x = fromX + getPosX() + getSpacing();
		float x1 = x - (4 * scale);
		float x2 = x + (12 * scale);
		
		int scoreLineSpacing = layout.getScoreLineSpacing();
		
		layout.setLineStyle(painter);
		if(y < fromY){
			for(int i = fromY;i > y;i -= scoreLineSpacing){
				painter.initPath();
				painter.setAntialias(false);
				painter.moveTo(x1,i);
				painter.lineTo(x2,i);
				painter.closePath();
			}
		}else if(y > (fromY + (scoreLineSpacing * 4))){
			for(int i = (fromY +(scoreLineSpacing * 5));i < (y + scoreLineSpacing);i += scoreLineSpacing){
				painter.initPath();
				painter.setAntialias(false);
				painter.moveTo(x1,i);
				painter.lineTo(x2,i);
				painter.closePath();
			}
		}
	}
	
	public void paintStroke(ViewLayout layout,TGPainter painter, int fromX, int fromY){
		int style = layout.getStyle();
		float scale = layout.getScale();
		float x = (fromX + getPosX() + getSpacing() + ( 12f * scale ));
		float y1 = 0;
		float y2 = 0;
		if((style & ViewLayout.DISPLAY_SCORE) != 0){
			float y = (fromY + getPaintPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES)); 
			y1 = (y + layout.getScoreLineSpacing());
			y2 = (y + (getMeasureImpl().getTrackImpl().getScoreHeight() - layout.getScoreLineSpacing()));
		}
		if((style & ViewLayout.DISPLAY_TABLATURE) != 0){
			float y = (fromY + getPaintPosition(TGTrackSpacing.POSITION_TABLATURE));
			y1 = (y + layout.getStringSpacing());
			y2 = (y + (getMeasureImpl().getTrackImpl().getTabHeight() - layout.getStringSpacing()));
		}
		else if((style & ViewLayout.DISPLAY_SCORE) != 0){
			float y = (fromY + getPaintPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES)); 
			y1 = (y + layout.getScoreLineSpacing());
			y2 = (y + (getMeasureImpl().getTrackImpl().getScoreHeight() - layout.getScoreLineSpacing()));
		}else{
			return;
		}
		if( getStroke().getDirection() == TGStroke.STROKE_UP ){
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo( x, y1 );
			painter.lineTo( x, y2 );
			painter.lineTo( x - (2.0f * scale), y2 - (5.0f * scale));
			painter.moveTo( x , y2 );
			painter.lineTo( x + (2.0f * scale), y2 - (5.0f * scale));
			painter.closePath();
		}else if( getStroke().getDirection() == TGStroke.STROKE_DOWN ){
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo( x, y2 );
			painter.lineTo( x, y1 );
			painter.lineTo( x - (2.0f * scale), y1 + (3.0f * scale));
			painter.moveTo( x , y1 );
			painter.lineTo( x + (2.0f * scale), y1 + (3.0f * scale));
			painter.closePath();
		}
	}
	
	public int getPaintPosition(int index){
		return getMeasureImpl().getTs().getPosition(index);
	}
	
	public TGBeatSpacing getBs(){
		return this.bs;
	}
	
	public void removeChord(){
		if(isChordBeat()){
			TGChordImpl chord = (TGChordImpl)getChord();
			chord.dispose();
		}
		super.removeChord();
	}
	
	public void dispose(){
		if(isChordBeat()){
			TGChordImpl chord = (TGChordImpl)getChord();
			chord.dispose();
		}
	}
}
