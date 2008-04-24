package org.herac.tuxguitar.gui.editors.tab;

import java.util.Iterator;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.layout.TrackSpacing;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.editors.tab.painters.TGNotePainter;
import org.herac.tuxguitar.gui.editors.tab.painters.TGSilencePainter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGTupleto;

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
	
	public TGBeatImpl(TGFactory factory){
		super(factory);
	}
	
	public int getPosX() {
		return this.posX;
	}
	
	public void setPosX(int posX) {
		this.posX = posX;
	}
	
	public int getWidth() {
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
	
	public void setPreviousBeat(TGBeatImpl previous){
		this.previous = previous;
	}
	
	public void setNextBeat(TGBeatImpl next){
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
	
	public void reset(){
		this.maxNote = null;
		this.minNote = null;
		this.usedStrings = new boolean[getMeasure().getTrack().stringCount()];
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
	}
	
	public void play(){
		if(!TuxGuitar.instance().getPlayer().isRunning()){
			new Thread(new Runnable() {
				public void run() {
					TuxGuitar.instance().getPlayer().playBeat(getMeasure().getTrack(),getNotes());
				}
			}).start();
		}
	}
	
	public void update(ViewLayout layout) {
		if(!isRestBeat()){
			this.joinedType = JOINED_TYPE_NONE_RIGHT;
			this.joinedGreaterThanQuarter = false;
			this.setJoin1(this);
			this.setJoin2(this);
			
			boolean noteJoined = false;
			boolean withPrev = false;
			
			//trato de unir con el componente anterior
			if (this.previous != null && !this.previous.isRestBeat()) {
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
			if (this.next != null && !this.next.isRestBeat() ) {
				if (getMeasureImpl().canJoin(layout.getSongManager(),this, this.next)) {
					if (this.next.getDuration().getValue() >= getDuration().getValue()) {
						this.setJoin2(this.next);
						if (this.previous == null || this.previous.isRestBeat() || this.previous.getDuration().getValue() < getDuration().getValue()) {
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
		}
	}
	/*
	public void paint(ViewLayout layout,TGPainter painter, int fromX, int fromY) {
		paint(layout, painter, fromX, fromY, false);
	}*/
	
	public void paint(ViewLayout layout,TGPainter painter, int fromX, int fromY/*,boolean playMode*/) {
		if(!layout.isPlayModeEnabled() && (layout.getStyle() & ViewLayout.DISPLAY_SCORE) != 0 ){
			paintExtraLines(painter, layout,fromX, fromY);
		}
		
		if(isRestBeat()){
			paintSilence(layout, painter, fromX, fromY);
		}
		else{
			Iterator notes = getNotes().iterator();
			while (notes.hasNext()) {
				TGNoteImpl note = (TGNoteImpl)notes.next();
				note.paint(layout,painter,fromX ,fromY);
			}
			if(!layout.isPlayModeEnabled()){
				paintBeat(layout, painter, fromX, fromY) ;
				
				if(isChordBeat()){
					TGChordImpl chord = (TGChordImpl)getChord();
					chord.paint(layout,painter,fromX,fromY);
				}
			}
		}
	}
	
	//----silence
	public void paintSilence(ViewLayout layout,TGPainter painter, int fromX, int fromY) {
		int style = layout.getStyle();
		int x = 0;
		int firstLineY = 0;
		int lineSpacing = 0;
		int lineCount = 0;
		float y = 0;
		float scale = 0;
		
		if((style & ViewLayout.DISPLAY_SCORE) != 0 ){
			x = fromX + getPosX() + getSpacing();
			firstLineY = fromY + getPaintPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES);
			lineSpacing = layout.getScoreLineSpacing();
			lineCount = 5;
			scale = (lineSpacing / 9.0f);
		}else{
			x = fromX + getPosX() + getSpacing() - 1;
			firstLineY = fromY + getPaintPosition(TrackSpacing.POSITION_TABLATURE);
			lineSpacing = layout.getStringSpacing();
			lineCount = getMeasure().getTrack().stringCount();
			scale = (lineSpacing / 10.0f);
		}
		
		setStyle(layout, painter,(layout.isPlayModeEnabled() && isPlaying(layout)));
		painter.initPath(TGPainter.PATH_FILL);
		
		int duration = getDuration().getValue();
		if(duration == TGDuration.WHOLE){
			y = firstLineY + ( lineCount <= 5 ? lineSpacing : lineSpacing * 2 );
			TGSilencePainter.paintWhole(painter, x, y , scale);
		}
		if(duration == TGDuration.HALF){
			y = firstLineY + ( lineCount <= 5 ? lineSpacing * 2 : lineSpacing * 3 ) - (scale * 3);
			TGSilencePainter.paintHalf(painter, x, y , scale);
		}
		if(duration == TGDuration.QUARTER){
			y = firstLineY + ((lineSpacing * (lineCount - 1)) / 2) - (scale * 8) ;
			TGSilencePainter.paintQuarter(painter, x, y, scale);
		}
		else if(duration == TGDuration.EIGHTH){
			y = firstLineY + ((lineSpacing * (lineCount - 1)) / 2) - (scale * 6) ;
			TGSilencePainter.paintEighth(painter, x, y, scale);
		}
		else if(duration == TGDuration.SIXTEENTH){
			y = firstLineY + ((lineSpacing * (lineCount - 1)) / 2) - (scale * 8) ;
			TGSilencePainter.paintSixteenth(painter, x, y, scale);
		}
		else if(duration == TGDuration.THIRTY_SECOND){
			y = firstLineY + ((lineSpacing * (lineCount - 1)) / 2) - (scale * 12) ;
			TGSilencePainter.paintThirtySecond(painter, x, y, scale);
		}
		else if(duration == TGDuration.SIXTY_FOURTH){
			y = firstLineY + ((lineSpacing * (lineCount - 1)) / 2) - (scale * 14) ;
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
		if(!getDuration().getTupleto().isEqual(TGTupleto.NORMAL)){
			layout.setTupletoStyle(painter);
			if((style & ViewLayout.DISPLAY_SCORE) != 0 ){
				painter.drawString(Integer.toString(getDuration().getTupleto().getEnters()), x,(fromY + getPaintPosition(TrackSpacing.POSITION_TUPLETO)));
			}else{
				painter.drawString(Integer.toString(getDuration().getTupleto().getEnters()),x,(fromY + getPaintPosition(TrackSpacing.POSITION_TUPLETO)));
			}
		}
	}
	
	public void setStyle(ViewLayout layout, TGPainter painter, boolean playMode){
		if((layout.getStyle() & ViewLayout.DISPLAY_SCORE) != 0 ){
			layout.setScoreSilenceStyle(painter, playMode);
		}else{
			layout.setTabSilenceStyle(painter, playMode);
		}
	}
	
	public void paintExtraLines(TGPainter painter,ViewLayout layout,int fromX, int fromY){
		if(!isRestBeat()){
			int scoreY = (fromY + getMeasureImpl().getTs().getPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES));
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
				painter.moveTo(x1,i);
				painter.lineTo(x2,i);
				painter.closePath();
			}
		}else if(y > (fromY + (scoreLineSpacing * 4))){
			for(int i = (fromY +(scoreLineSpacing * 5));i < (y + scoreLineSpacing);i += scoreLineSpacing){
				painter.initPath();
				painter.moveTo(x1,i);
				painter.lineTo(x2,i);
				painter.closePath();
			}
		}
	}
	
	public void paintBeat(ViewLayout layout,TGPainter painter, int fromX, int fromY){
		if(!isRestBeat() ){
			int style = layout.getStyle();
			int spacing = getSpacing();
			
			if((style & ViewLayout.DISPLAY_SCORE) != 0){
				paintScoreBeat(layout, painter, fromX, fromY+getPaintPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES), spacing);
			}
			if((style & ViewLayout.DISPLAY_TABLATURE) != 0 && (style & ViewLayout.DISPLAY_SCORE) == 0){
				paintTablatureBeat(layout, painter, fromX, fromY + getPaintPosition(TrackSpacing.POSITION_TABLATURE), spacing);
			}
		}
	}
	
	public void paintTablatureBeat(ViewLayout layout,TGPainter painter, int fromX, int fromY, int spacing){
		if(!isRestBeat() ){
			int xMove = 2;
			int stringSpacing = layout.getStringSpacing();
			int x = ( fromX + getPosX() + spacing + xMove );
			
			int y1 = (fromY + getMeasureImpl().getTrackImpl().getTabHeight() + (stringSpacing / 2));
			int y2 = (fromY + getMeasureImpl().getTrackImpl().getTabHeight() + ((stringSpacing / 2) * 5));
			
			if (getDuration().getValue() >= TGDuration.QUARTER) {
				layout.setTabNoteFooterStyle(painter);
				painter.initPath();
				
				boolean painting = false;
				boolean[] usedStrings = getUsedStrings();
				for (int i = 0; i < usedStrings.length; i++) {
					if (!usedStrings[i] && painting) {
						int stringPosition = (fromY + (stringSpacing * (i + 1)) - stringSpacing);
						int posY1 = stringPosition - (stringSpacing / 2);
						int posY2 = stringPosition + (stringSpacing / 2);
						posY2 = ((posY2 - posY1 < stringSpacing)?posY2 + 1:posY2);
						painter.moveTo(x, posY1);
						painter.lineTo(x, posY2);
					}
					painting = ( painting || usedStrings[i] );
				}
				painter.moveTo(x, y1);
				painter.lineTo(x, y2);
				painter.closePath();
				if (getDuration().getValue() >= TGDuration.EIGHTH) {
					int x1 = 0;
					int x2 = 0;
					int scale = (layout.getStringSpacing() / 2);
					int joinedType = getJoinedType();
					if(joinedType == TGBeatImpl.JOINED_TYPE_NONE_RIGHT){
						x1 = getPosX() + xMove + spacing;
						x2 = getPosX() + xMove + spacing + 6;
					}else if(joinedType == TGBeatImpl.JOINED_TYPE_NONE_LEFT){
						x1 = getPosX() + xMove + spacing - 5;
						x2 = getPosX() + xMove + spacing;
					}else{
						x1 = getJoin1().getPosX() + xMove + getJoin1().getSpacing();
						x2 = getJoin2().getPosX() + xMove + getJoin2().getSpacing();
					}
					int index = ( getDuration().getIndex() - 2);
					if(index > 0){
						painter.setLineWidth(2);
						painter.initPath();
						for(int i = index; i > 0 ;i --){
							painter.moveTo(fromX + x1, y2 + (scale - (i * scale)));
							painter.lineTo(fromX + x2, y2 + (scale - (i * scale)));
						}
						painter.closePath();
						painter.setLineWidth(1);
					}
				}
			} else if (getDuration().getValue() == TGDuration.HALF) {
				layout.setTabNoteFooterStyle(painter);
				painter.initPath();
				painter.moveTo(x, (y1 + ((y2 - y1) / 2)));
				painter.lineTo(x, y2);
				painter.closePath();
			}
			
			//-------------puntillo--------------------------------------
			if (getDuration().isDotted() || getDuration().isDoubleDotted()) {
				int joinedType = getJoinedType();
				
				float scale = (stringSpacing / 10.0f);
				float posX = ((getDuration().getValue() > TGDuration.WHOLE)?((joinedType == TGBeatImpl.JOINED_TYPE_NONE_RIGHT || joinedType == TGBeatImpl.JOINED_TYPE_RIGHT)?(x+ (4.0f * scale)):(x- (5.0f * scale))):x);
				float posY = (y2 - ((getDuration().getValue() >= TGDuration.EIGHTH)? ((stringSpacing / 2) * (getDuration().getIndex() - 2)):(1.0f * scale)));
				paintDot(layout, painter, posX, posY,scale);
			}
			
			//-------------tresillo--------------------------------------
			if (!getDuration().getTupleto().isEqual(TGTupleto.NORMAL)) {
				layout.setTupletoStyle(painter);
				painter.drawString(Integer.toString(getDuration().getTupleto().getEnters()), x - 3,((fromY - getPaintPosition(TrackSpacing.POSITION_TABLATURE)) + getPaintPosition(TrackSpacing.POSITION_TUPLETO)));
			}
		}
	}
	
	public void paintScoreBeat(ViewLayout layout,TGPainter painter, int fromX, int fromY, int spacing){
		int vX = ( fromX + getPosX() + spacing );
		
		//TUPLETO
		if (!getDuration().getTupleto().isEqual(TGTupleto.NORMAL)) {
			layout.setTupletoStyle(painter);
			painter.drawString(Integer.toString(getDuration().getTupleto().getEnters()),vX ,((fromY - getPaintPosition(TrackSpacing.POSITION_SCORE_MIDDLE_LINES)) + getPaintPosition(TrackSpacing.POSITION_TUPLETO)));
		}
		//dibujo el pie
		if(getDuration().getValue() >= TGDuration.HALF){
			layout.setScoreNoteFooterStyle(painter);
			
			float scale = layout.getScale();
			float lineSpacing = layout.getScoreLineSpacing();
			int direction = this.group.getDirection();
			int key = getMeasure().getKeySignature();
			int clef = getMeasure().getClef();
			
			int xMove = (direction == TGBeatGroup.DIRECTION_UP ? layout.getResources().getScoreNoteWidth() : 0);
			int yMove = (direction == TGBeatGroup.DIRECTION_UP ? ((layout.getScoreLineSpacing() / 3) + 1) : ((layout.getScoreLineSpacing() / 3) * 2));
			
			int vY1 = fromY + ( direction == TGBeatGroup.DIRECTION_DOWN ? this.maxNote.getScorePosY() : this.minNote.getScorePosY() );
			int vY2 = fromY + this.group.getY2(layout,getPosX() + spacing,key,clef);
			
			painter.initPath();
			painter.moveTo(vX + xMove, vY1 + yMove);
			painter.lineTo(vX + xMove, vY2);
			painter.closePath();
			
			if (getDuration().getValue() >= TGDuration.EIGHTH) {
				int index =  ( getDuration().getIndex() - 3);
				if(index >= 0){
					int dir = (direction == TGBeatGroup.DIRECTION_DOWN)?1:-1;
					int joinedType = getJoinedType();
					boolean joinedGreaterThanQuarter = isJoinedGreaterThanQuarter();
					
					if((joinedType == TGBeatImpl.JOINED_TYPE_NONE_LEFT || joinedType == TGBeatImpl.JOINED_TYPE_NONE_RIGHT) && !joinedGreaterThanQuarter){
						float hX = (fromX + xMove + getPosX() + spacing);
						float hY = ( (fromY + this.group.getY2(layout,getPosX() + spacing,key,clef)) - ( (lineSpacing * 2)* dir )) ;
						for(int i = 0; i <= index; i ++){
							painter.initPath(TGPainter.PATH_FILL);
							TGNotePainter.paintFooter(painter,hX,(hY - ( (i * (lineSpacing / 2.0f)) * dir)),dir,lineSpacing);
							painter.closePath();
						}
					}else{
						int hX1 = 0;
						int hX2 = 0;
						if(joinedType == TGBeatImpl.JOINED_TYPE_NONE_RIGHT){
							hX1 = getPosX() + spacing;
							hX2 = getPosX() + spacing + 6;
						}else if(joinedType == TGBeatImpl.JOINED_TYPE_NONE_LEFT){
							hX1 = getPosX() + spacing - 5;
							hX2 = getPosX() + spacing;
						}else{
							hX1 = getJoin1().getPosX() + getJoin1().getSpacing();
							hX2 = getJoin2().getPosX() + getJoin2().getSpacing();
						}
						int hY1 = fromY + this.group.getY2(layout,hX1,key,clef);
						int hY2 = fromY + this.group.getY2(layout,hX2,key,clef);
						//painter.setLineWidth(3);
						painter.setLineWidth(Math.max(1,Math.round(3f * scale)));
						painter.initPath();
						for(int i = 0; i <= index; i ++){
							painter.moveTo(fromX + xMove + hX1, hY1 - ( (i * (5f * scale)) * dir));
							painter.lineTo(fromX + xMove + hX2, hY2 - ( (i * (5f * scale)) * dir));
							//painter.moveTo(fromX + xMove + hX1, hY1 - ( (i * (scale/1.6f)) * dir));
							//painter.lineTo(fromX + xMove + hX2, hY2 - ( (i * (scale/1.6f)) * dir));
						}
						painter.closePath();
						painter.setLineWidth(1);
					}
				}
			}
		}
	}
	
	public void paintDot(ViewLayout layout,TGPainter painter,float fromX, float fromY,float scale){
		float dotSize = (3.0f * scale);
		float posX = fromX;
		float posY = fromY;
		layout.setDotStyle(painter);
		painter.initPath(TGPainter.PATH_FILL);
		painter.moveTo(posX - (dotSize / 2), posY - (dotSize / 2));
		painter.addOval(posX - (dotSize / 2), posY - (dotSize / 2), dotSize,dotSize);
		if(getDuration().isDoubleDotted()){
			painter.moveTo(posX + (dotSize + 2) - (dotSize / 2), posY - (dotSize / 2));
			painter.addOval(posX + (dotSize + 2) - (dotSize / 2), posY - (dotSize / 2), dotSize,dotSize);
		}
		painter.closePath();
	}
	
	public int getPaintPosition(int index){
		return getMeasureImpl().getTs().getPosition(index);
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
