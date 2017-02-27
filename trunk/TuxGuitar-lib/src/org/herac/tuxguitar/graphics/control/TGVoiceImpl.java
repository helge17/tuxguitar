package org.herac.tuxguitar.graphics.control;

import java.util.Iterator;

import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.control.painters.TGNotePainter;
import org.herac.tuxguitar.graphics.control.painters.TGSilencePainter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVoice;

public class TGVoiceImpl extends TGVoice{
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
	
	private float width;
	private TGNoteImpl maxNote;
	private TGNoteImpl minNote;
	private boolean[] usedStrings;
	private int joinedType;
	private boolean joinedGreaterThanQuarter;
	private TGVoiceImpl join1;
	private TGVoiceImpl join2;
	private TGVoiceImpl previous;
	private TGVoiceImpl next;
	private TGBeatGroup group;
	
	private boolean hiddenSilence;
	
	private float maxY;
	private float minY;
	private float silenceY;
	private float silenceHeight;
	
	private int maxString;
	private int minString;
	
	public TGVoiceImpl(TGFactory factory,int index){
		super(factory, index);
	}
	
	public float getPosX() {
		return this.getBeatImpl().getPosX();
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public TGNoteImpl getMinNote(){
		return this.minNote;
	}
	
	public TGNoteImpl getMaxNote(){
		return this.maxNote;
	}
	
	public TGBeatImpl getBeatImpl() {
		return (TGBeatImpl)getBeat();
	}
	
	public TGMeasureImpl getMeasureImpl() {
		return (TGMeasureImpl)getBeat().getMeasure();
	}
	
	public boolean[] getUsedStrings() {
		if( this.usedStrings == null ){
			this.usedStrings = new boolean[getBeat().getMeasure().getTrack().stringCount()];
		}
		return this.usedStrings;
	}
	
	public TGVoiceImpl getJoin1() {
		return this.join1;
	}
	
	public void setJoin1(TGVoiceImpl join1) {
		this.join1 = join1;
	}
	
	public TGVoiceImpl getJoin2() {
		return this.join2;
	}
	
	public void setJoin2(TGVoiceImpl join2) {
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
	
	public void setPreviousBeat(TGVoiceImpl previous){
		this.previous = previous;
	}
	
	public void setNextBeat(TGVoiceImpl next){
		this.next = next;
	}
	
	public TGBeatGroup getBeatGroup() {
		return this.group;
	}
	
	public void setBeatGroup(TGBeatGroup group) {
		this.group = group;
	}
	
	public boolean isPlaying(TGLayout layout){
		return layout.getComponent().isRunning(this.getBeat());
	}
	
	public void reset(){
		this.maxNote = null;
		this.minNote = null;
		this.hiddenSilence = false;
		this.usedStrings = new boolean[getBeat().getMeasure().getTrack().stringCount()];
		this.maxString = 1;
		this.minString = getBeat().getMeasure().getTrack().stringCount();
		this.group = null;
		this.previous = null;
		this.next = null;
	}
	
	public void check(TGNoteImpl note){
		int value = note.getRealValue();
		if(this.maxNote == null || value > this.maxNote.getRealValue()){
			this.maxNote = note;
		}
		if(this.minNote == null || value < this.minNote.getRealValue()){
			this.minNote = note;
		}
		this.getUsedStrings();
		this.usedStrings[note.getString() - 1] = true;
		
		if( note.getString() > this.maxString ){
			this.maxString = note.getString();
		}
		if( note.getString() < this.minString ){
			this.minString = note.getString();
		}
	}
	
	public void update(TGLayout layout) {
		this.minY = 0;
		this.maxY = 0;
		if(isRestVoice()){
			this.updateSilenceSpacing(layout);
		}
		else{
			this.updateNoteVoice(layout);
		}
	}
	
	public void updateNoteVoice(TGLayout layout) {
		this.joinedType = JOINED_TYPE_NONE_RIGHT;
		this.joinedGreaterThanQuarter = false;
		this.setJoin1(this);
		this.setJoin2(this);
		
		boolean noteJoined = false;
		boolean withPrev = false;
		
		//trato de unir con el componente anterior
		if (this.previous != null && !this.previous.isRestVoice()) {
			if (getMeasureImpl().canJoin(layout.getSongManager(),this, this.previous)) {
				withPrev = true;
				if (this.previous.getDuration().getValue() >= getDuration().getValue()) {
					this.setJoin1(this.previous);
					this.setJoin2(this);
					this.joinedType = JOINED_TYPE_LEFT;
					noteJoined = true;
				}
				if (this.previous.getDuration().getValue() > TGDuration.QUARTER){
					this.joinedGreaterThanQuarter = true;
				}
			}
		}
		
		//trato de unir con el componente que le sigue
		if (this.next != null && !this.next.isRestVoice() ) {
			if (getMeasureImpl().canJoin(layout.getSongManager(),this, this.next)) {
				if (this.next.getDuration().getValue() >= getDuration().getValue()) {
					this.setJoin2(this.next);
					if (this.previous == null || this.previous.isRestVoice() || this.previous.getDuration().getValue() < getDuration().getValue()) {
						this.setJoin1(this);
					}
					noteJoined = true;
					this.joinedType = JOINED_TYPE_RIGHT;
				}
				if (this.next.getDuration().getValue() > TGDuration.QUARTER){
					this.joinedGreaterThanQuarter = true;
				}
			}
		}
		
		//si no hubo union decido para que lado girar la figura
		if (!noteJoined && withPrev) {
			this.joinedType = JOINED_TYPE_NONE_LEFT;
		}
		
		if( (layout.getStyle() & TGLayout.DISPLAY_TABLATURE) != 0 && (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0){
			this.minY = 0;
			this.maxY = getBeatImpl().getMeasureImpl().getTrackImpl().getTabHeight();
			if( getBeatGroup().getDirection() == TGBeatGroup.DIRECTION_DOWN ){
				this.maxY += (((layout.getStringSpacing() / 2) * 5) + 1);
			}else{
				this.minY -= (((layout.getStringSpacing() / 2) * 5) + 1);
			}
		}
	}
	
	public void updateSilenceSpacing(TGLayout layout) {
		this.silenceY = 0;
		this.silenceHeight = 0;
		
		if(!isHiddenSilence()){
			int style = layout.getStyle();
			int lineCount = 0;
			float scale = 0;
			float lineSpacing = 0;
			if((style & TGLayout.DISPLAY_SCORE) != 0 ){
				lineSpacing = layout.getScoreLineSpacing();
				lineCount = 5;
				scale = (lineSpacing / 9.0f);
			}else{
				lineSpacing = layout.getStringSpacing();
				lineCount = getBeat().getMeasure().getTrack().stringCount();
				scale = (lineSpacing / 10.0f);
			}
			
			int duration = getDuration().getValue();
			if(duration == TGDuration.WHOLE){
				this.silenceHeight = (scale * 3.6513581f);
				this.silenceY = ( lineCount <= 5 ? lineSpacing : lineSpacing * 2 );
			}
			else if(duration == TGDuration.HALF){
				this.silenceHeight = (scale * 3.6513581f);
				this.silenceY = ( lineCount <= 5 ? lineSpacing * 2 : lineSpacing * 3 ) - this.silenceHeight;
			}
			else if(duration == TGDuration.QUARTER){
				this.silenceHeight = (scale * 16);
				this.silenceY = ((lineSpacing * (lineCount - 1)) / 2) - (this.silenceHeight / 2);
			}
			else if(duration == TGDuration.EIGHTH){
				this.silenceHeight = (scale * 12);
				this.silenceY = ((lineSpacing * (lineCount - 1)) / 2) - (this.silenceHeight / 2);
			}
			else if(duration == TGDuration.SIXTEENTH){
				this.silenceHeight = (scale * 16);
				this.silenceY = ((lineSpacing * (lineCount - 1)) / 2) - (this.silenceHeight / 2);
			}
			else if(duration == TGDuration.THIRTY_SECOND){
				this.silenceHeight = (scale * 24);
				this.silenceY = ((lineSpacing * (lineCount - 1)) / 2) - (this.silenceHeight / 2);
			}
			else if(duration == TGDuration.SIXTY_FOURTH){
				this.silenceHeight = (scale * 28);
				this.silenceY = ((lineSpacing * (lineCount - 1)) / 2) - (this.silenceHeight / 2);
			}
			
			for( int v = 0; v < getBeat().countVoices(); v ++){
				if( v != getIndex() ){
					TGVoiceImpl voice = getBeatImpl().getVoiceImpl( v );
					if( !voice.isEmpty() ){
						if( voice.isRestVoice()){
							if( !voice.isHiddenSilence() ){
								float maxSilenceHeight = (lineSpacing * 3);
								float firstPosition = (this.silenceY - (maxSilenceHeight / getBeat().countVoices()));
								this.silenceY = (firstPosition + ( maxSilenceHeight * getIndex() ));
							}
						}
						else if((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 ){
							int direction = voice.getBeatGroup().getDirection();
							float y1 = voice.getBeatGroup().getY1(layout,voice.getMinNote(),getMeasureImpl().getKeySignature(),getMeasureImpl().getClef());
							float y2 = voice.getBeatGroup().getY1(layout,voice.getMaxNote(),getMeasureImpl().getKeySignature(),getMeasureImpl().getClef());
							
							if(direction == TGBeatGroup.DIRECTION_UP){
								float position = (y1 + (lineSpacing * 2));
								if( position > this.silenceY ){
									this.silenceY = position;
								}
							}else if(direction == TGBeatGroup.DIRECTION_DOWN){
								float position = (y2 - (this.silenceHeight + lineSpacing));
								if( position < this.silenceY ){
									this.silenceY = position;
								}
							}
						}else if((layout.getStyle() & TGLayout.DISPLAY_TABLATURE) != 0 ){
							int direction = voice.getBeatGroup().getDirection();
							if(direction == TGBeatGroup.DIRECTION_UP){
								float position = (lineSpacing * voice.getMaxString() );
								if( position > this.silenceY ){
									this.silenceY = position;
								}
							}
							else if(direction == TGBeatGroup.DIRECTION_DOWN){
								float position = ((lineSpacing * (voice.getMinString() - 1) ) - (this.silenceHeight + lineSpacing));
								if( position < this.silenceY ){
									this.silenceY = position;
								}
							}
						}
					}
				}
			}
			this.minY = this.silenceY;
			this.maxY = (this.silenceY + this.silenceHeight);
		}
	}
	
	public void paint(TGLayout layout,TGPainter painter, float fromX, float fromY) {
		if(!isEmpty()){
			if( isRestVoice() && !isHiddenSilence() ){
				paintSilence(layout, painter, fromX, fromY);
			}
			else{
				Iterator<TGNote> notes = getNotes().iterator();
				while (notes.hasNext()) {
					TGNoteImpl note = (TGNoteImpl)notes.next();
					note.paint(layout,painter,fromX ,fromY);
				}
				if(!layout.isPlayModeEnabled()){
					paintBeat(layout, painter, fromX, fromY) ;
				}
			}
		}
	}
	
	//----silence
	public void paintSilence(TGLayout layout,TGPainter painter, float fromX, float fromY) {
		int style = layout.getStyle();
		float x = 0;
		float lineSpacing = 0;
		float y = 0;
		float scale = 0;
		
		if((style & TGLayout.DISPLAY_SCORE) != 0 ){
			x = fromX + getPosX() + getBeatImpl().getSpacing(layout);
			y = fromY + getPaintPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES) + this.silenceY;
			lineSpacing = layout.getScoreLineSpacing();
			scale = (lineSpacing / 9.0f);
		}else{
			x = fromX + getPosX() + getBeatImpl().getSpacing(layout) - 1;
			y = fromY + getPaintPosition(TGTrackSpacing.POSITION_TABLATURE) + this.silenceY;
			lineSpacing = layout.getStringSpacing();
			scale = (lineSpacing / 10.0f);
		}
		
		setStyle(layout, painter,(layout.isPlayModeEnabled() && isPlaying(layout)));
		painter.initPath(TGPainter.PATH_FILL);
		
		int duration = getDuration().getValue();
		if(duration == TGDuration.WHOLE){
			TGSilencePainter.paintWhole(painter, x, y , scale);
		}
		else if(duration == TGDuration.HALF){
			TGSilencePainter.paintHalf(painter, x, y , scale);
		}
		else if(duration == TGDuration.QUARTER){
			TGSilencePainter.paintQuarter(painter, x, y, scale);
		}
		else if(duration == TGDuration.EIGHTH){
			TGSilencePainter.paintEighth(painter, x, y, scale);
		}
		else if(duration == TGDuration.SIXTEENTH){
			TGSilencePainter.paintSixteenth(painter, x, y, scale);
		}
		else if(duration == TGDuration.THIRTY_SECOND){
			TGSilencePainter.paintThirtySecond(painter, x, y, scale);
		}
		else if(duration == TGDuration.SIXTY_FOURTH){
			TGSilencePainter.paintSixtyFourth(painter, x, y, scale);
		}
		
		painter.closePath();
		
		if(getDuration().isDotted() || getDuration().isDoubleDotted()){
			layout.setDotStyle(painter);
			painter.initPath();
			painter.moveTo(x + 10,y +1);
			painter.addOval(x + 10,y +1,1,1);
			if(getDuration().isDoubleDotted()){
				painter.moveTo(x + 13,y +1);
				painter.addOval(x + 13,y +1,1,1);
			}
			painter.closePath();
		}
	}
	
	public void setStyle(TGLayout layout, TGPainter painter, boolean playMode){
		if((layout.getStyle() & TGLayout.DISPLAY_SCORE) != 0 ){
			layout.setScoreSilenceStyle(painter, playMode);
		}else{
			layout.setTabSilenceStyle(painter, playMode);
		}
	}
	
	public void paintBeat(TGLayout layout,TGPainter painter, float fromX, float fromY){
		if(!isRestVoice() ){
			int style = layout.getStyle();
			float spacing = getBeatImpl().getSpacing(layout);
			
			if((style & TGLayout.DISPLAY_SCORE) != 0){
				paintScoreBeat(layout, painter, fromX, fromY + getPaintPosition(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES), spacing);
			}
			if((style & TGLayout.DISPLAY_TABLATURE) != 0 && (style & TGLayout.DISPLAY_SCORE) == 0){
				paintTablatureBeat(layout, painter, fromX, fromY + getPaintPosition(TGTrackSpacing.POSITION_TABLATURE), spacing);
			}
		}
	}
	
	public void paintTablatureBeat(TGLayout layout,TGPainter painter, float fromX, float fromY, float spacing){
		if(!isRestVoice() ){
			float scale = layout.getScale();
			float xMove = (2f * scale);
			float x = ( fromX + getPosX() + spacing + xMove );
			float y1 = 0;
			float y2 = 0;
			float verticalLineWidth = layout.getLineWidth(1);
			float horizontalLineWidth = layout.getLineWidth(2);
			float stringSpacing = layout.getStringSpacing();
			float direction = (getBeatGroup().getDirection() == TGBeatGroup.DIRECTION_DOWN ? 1f : -1f);
			if( getBeatGroup().getDirection() == TGBeatGroup.DIRECTION_DOWN ){
				y1 = (fromY + getMeasureImpl().getTrackImpl().getTabHeight() + (stringSpacing / 2));
				y2 = (fromY + getMeasureImpl().getTrackImpl().getTabHeight() + ((stringSpacing / 2) * 5));
			}else{
				y1 = (fromY - (stringSpacing / 2) - horizontalLineWidth);
				y2 = (fromY - ((stringSpacing / 2) * 5));
			}
			if (getDuration().getValue() >= TGDuration.QUARTER) {
				layout.setTabNoteFooterStyle(painter);
				painter.initPath(TGPainter.PATH_DRAW | TGPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.setLineWidth( verticalLineWidth );
				painter.moveTo(x, y1);
				painter.lineTo(x, y2);
				if (getDuration().getValue() >= TGDuration.EIGHTH) {
					float x1 = 0;
					float x2 = 0;
					int joinedType = getJoinedType();
					if(joinedType == TGVoiceImpl.JOINED_TYPE_NONE_RIGHT){
						x1 = getPosX() + xMove + spacing;
						x2 = getPosX() + xMove + spacing + (6 * scale);
					}else if(joinedType == TGVoiceImpl.JOINED_TYPE_NONE_LEFT){
						x1 = getPosX() + xMove + spacing - (5 * scale);
						x2 = getPosX() + xMove + spacing;
					}else{
						x1 = getJoin1().getPosX() + xMove + getJoin1().getBeatImpl().getSpacing(layout);
						x2 = getJoin2().getPosX() + xMove + getJoin2().getBeatImpl().getSpacing(layout);
					}
					int index = ( getDuration().getIndex() - 2);
					if(index > 0){
						float height = (layout.getStringSpacing() / 2);
						for(int i = index; i > 0 ;i --){
							painter.moveTo(fromX + x1, y2 + ((height - (i * height)) * direction));
							painter.lineTo(fromX + x2, y2 + ((height - (i * height)) * direction));
							painter.lineTo(fromX + x2, y2 + ((height - (i * height)) * direction) + (horizontalLineWidth * direction));
							painter.lineTo(fromX + x1, y2 + ((height - (i * height)) * direction) + (horizontalLineWidth * direction));
							painter.lineTo(fromX + x1, y2 + ((height - (i * height)) * direction));
						}
					}
				}
				painter.closePath();
			} else if (getDuration().getValue() == TGDuration.HALF) {
				layout.setTabNoteFooterStyle(painter);
				painter.initPath();
				painter.setAntialias(false);
				painter.setLineWidth( verticalLineWidth );
				painter.moveTo(x, (y1 + ((y2 - y1) / 2)));
				painter.lineTo(x, y2);
				painter.closePath();
			}
			
			//-------------puntillo--------------------------------------
			if (getDuration().isDotted() || getDuration().isDoubleDotted()) {
				int joinedType = getJoinedType();
				float posX = ((getDuration().getValue() > TGDuration.WHOLE)?((joinedType == TGVoiceImpl.JOINED_TYPE_NONE_RIGHT || joinedType == TGVoiceImpl.JOINED_TYPE_RIGHT)?(x+ (4.0f * scale)):(x- (5.0f * scale))):x);
				float posY = (y2 - ((getDuration().getValue() >= TGDuration.EIGHTH ? ((stringSpacing / 2f) * (getDuration().getIndex() - 2)) : scale) * direction));
				paintDot(layout, painter, posX, posY,scale);
			}
		}
	}
	
	public void paintScoreBeat(TGLayout layout,TGPainter painter, float fromX, float fromY, float spacing){
		float vX = ( fromX + getPosX() + spacing );
		
		//dibujo el pie
		if( getDuration().getValue() >= TGDuration.HALF ){
			layout.setScoreNoteFooterStyle(painter);
			
			float scale = layout.getScale();
			float lineSpacing = layout.getScoreLineSpacing();
			int direction = this.group.getDirection();
			int dir = (direction == TGBeatGroup.DIRECTION_DOWN ? 1 : -1);
			int key = getBeat().getMeasure().getKeySignature();
			int clef = getBeat().getMeasure().getClef();
			
			float scoreNoteWidth = layout.getScoreNoteWidth();
			float xMove = (direction == TGBeatGroup.DIRECTION_UP ? scoreNoteWidth : 0f);
			float yMove = ((lineSpacing / 2f) + (((lineSpacing / 10f) * 1.20f)) * dir);
			
			float vY1 = fromY + ( direction == TGBeatGroup.DIRECTION_DOWN ? this.maxNote.getScorePosY() : this.minNote.getScorePosY() );
			float vY2 = fromY + this.group.getY2(layout,getPosX() + spacing, key, clef);
			
			painter.setLineWidth(layout.getLineWidth(0));
			painter.initPath(TGPainter.PATH_FILL);
			painter.moveTo(vX + xMove - (0.5f * scale), vY1 + yMove);
			painter.lineTo(vX + xMove + (0.5f * scale), vY1 + yMove);
			painter.lineTo(vX + xMove + (0.5f * scale), vY2);
			painter.lineTo(vX + xMove - (0.5f * scale), vY2);
			painter.moveTo(vX + xMove - (0.5f * scale), vY1 + yMove);
			painter.closePath();
			
			if( getDuration().getValue() >= TGDuration.EIGHTH ) {
				int index =  ( getDuration().getIndex() - 3);
				if( index >= 0 ) {
					int joinedType = getJoinedType();
					boolean joinedGreaterThanQuarter = isJoinedGreaterThanQuarter();
					
					if((joinedType == TGVoiceImpl.JOINED_TYPE_NONE_LEFT || joinedType == TGVoiceImpl.JOINED_TYPE_NONE_RIGHT) && !joinedGreaterThanQuarter){
						float hX = (fromX + xMove + getPosX() + spacing - (0.5f * scale));
						float hY = ((fromY + this.group.getY2(layout,getPosX() + spacing,key,clef)) - ( (lineSpacing * 2)* dir )) ;
						for(int i = 0; i <= index; i ++){
							painter.initPath(TGPainter.PATH_FILL);
							TGNotePainter.paintFooter(painter, hX, (hY - ( (i * (lineSpacing / 2.0f)) * dir)),dir,lineSpacing);
							painter.closePath();
						}
					}else{
						float hX1 = 0;
						float hX2 = 0;
						if(joinedType == TGVoiceImpl.JOINED_TYPE_NONE_RIGHT){
							hX1 = getPosX() + spacing;
							hX2 = getPosX() + spacing + (6f * scale);
						}else if(joinedType == TGVoiceImpl.JOINED_TYPE_NONE_LEFT){
							hX1 = getPosX() + spacing - (5f * scale);
							hX2 = getPosX() + spacing;
						}else{
							hX1 = getJoin1().getPosX() + getJoin1().getBeatImpl().getSpacing(layout);
							hX2 = getJoin2().getPosX() + getJoin2().getBeatImpl().getSpacing(layout);
						}
						float hY1 = fromY + this.group.getY2(layout,hX1,key,clef);
						float hY2 = fromY + this.group.getY2(layout,hX2,key,clef);
						
						painter.initPath(TGPainter.PATH_FILL);
						for(int i = 0; i <= index; i ++){
							painter.moveTo(fromX + xMove + hX1 - (0.5f * scale), hY1 - ((i * (5f * scale)) * dir) - (1.5f * scale));
							painter.lineTo(fromX + xMove + hX1 - (0.5f * scale), hY1 - ((i * (5f * scale)) * dir) + (1.5f * scale));
							painter.lineTo(fromX + xMove + hX2 + (0.5f * scale), hY2 - ((i * (5f * scale)) * dir) + (1.5f * scale));
							painter.lineTo(fromX + xMove + hX2 + (0.5f * scale), hY2 - ((i * (5f * scale)) * dir) - (1.5f * scale));
							painter.lineTo(fromX + xMove + hX1 - (0.5f * scale), hY1 - ((i * (5f * scale)) * dir) - (1.5f * scale));
						}
						painter.closePath();
					}
				}
			}
		}
	}
	
	public void paintDot(TGLayout layout,TGPainter painter,float fromX, float fromY,float scale){
		float dotSize = (3.0f * scale);
		float posX = fromX;
		float posY = fromY;
		layout.setDotStyle(painter);
		painter.setLineWidth(layout.getLineWidth(0));
		painter.initPath(TGPainter.PATH_FILL);
		painter.moveTo(posX - (dotSize / 2), posY - (dotSize / 2));
		painter.addOval(posX - (dotSize / 2), posY - (dotSize / 2), dotSize,dotSize);
		if(getDuration().isDoubleDotted()){
			painter.moveTo(posX + (dotSize + 2) - (dotSize / 2), posY - (dotSize / 2));
			painter.addOval(posX + (dotSize + 2) - (dotSize / 2), posY - (dotSize / 2), dotSize,dotSize);
		}
		painter.closePath();
	}
	
	public float getPaintPosition(int index){
		return getMeasureImpl().getTs().getPosition(index);
	}
	
	public int getMaxString() {
		return this.maxString;
	}
	
	public int getMinString() {
		return this.minString;
	}
	
	public float getMaxY() {
		return this.maxY;
	}
	
	public float getMinY() {
		return this.minY;
	}
	
	public boolean isHiddenSilence() {
		return this.hiddenSilence;
	}
	
	public void setHiddenSilence(boolean hiddenSilence) {
		this.hiddenSilence = hiddenSilence;
	}
}
