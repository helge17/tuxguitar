/*
 * Created on 04-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGLayoutVertical extends TGLayout{
	
	private float maximumWidth;
	private float marginLeft;
	private float marginRight;
	
	public TGLayoutVertical(TGController controller,int style){
		super(controller,style);
	}
	
	public int getMode(){
		return MODE_VERTICAL;
	}
	
	public void paintSong(TGPainter painter,TGRectangle clientArea, float fromX, float fromY) {
		this.marginLeft = getFirstMeasureSpacing();
		this.marginRight = 10;
		this.maximumWidth = (clientArea.getWidth() - (this.marginLeft + this.marginRight));
		this.setHeight(0);
		this.setWidth(0);
		this.clearTrackPositions();
		
		int style = getStyle();
		int number = getComponent().getTrackSelection();
		float posY = Math.round(fromY + getFirstTrackSpacing());
		float height = getFirstTrackSpacing();
		float lineHeight = 0;
		
		int measureCount = getSong().countMeasureHeaders();
		int nextMeasureIndex = 0;
		while(measureCount > nextMeasureIndex){
			TempLine line = null;
			Iterator<TGTrack> tracks = getSong().getTracks();
			while(tracks.hasNext()){
				TGTrackImpl track = (TGTrackImpl) tracks.next();
				if(number < 0 || track.getNumber() == number){
					
					TGTrackSpacing ts = new TGTrackSpacing(this) ;
					ts.setSize(TGTrackSpacing.POSITION_SCORE_MIDDLE_LINES, ((style & DISPLAY_SCORE) != 0 ?( (getScoreLineSpacing() * 5) ):0));
					
					if(nextMeasureIndex == 0){
						((TGLyricImpl)track.getLyrics()).start();
					}
					
					line = getTempLines(track,nextMeasureIndex,ts);
					if( (style & DISPLAY_SCORE) != 0 ){
						ts.setSize(TGTrackSpacing.POSITION_SCORE_UP_LINES, Math.abs(line.minY));
						if(line.maxY > track.getScoreHeight()){
							ts.setSize(TGTrackSpacing.POSITION_SCORE_DOWN_LINES, (line.maxY - track.getScoreHeight()) );
						}
					}
					if((style & DISPLAY_TABLATURE) != 0){
						ts.setSize(TGTrackSpacing.POSITION_TABLATURE_TOP_SEPARATOR, ((style & DISPLAY_SCORE) != 0 ? getMinScoreTabSpacing() : Math.max(Math.abs(line.minY), getStringSpacing()) ));
						ts.setSize(TGTrackSpacing.POSITION_TABLATURE, ((style & DISPLAY_SCORE) != 0 ?  track.getTabHeight() + getStringSpacing() + 1 : Math.max( line.maxY, track.getTabHeight() + getStringSpacing() + 1) ));
					}
					ts.setSize(TGTrackSpacing.POSITION_LYRIC,10);
					checkDefaultSpacing(ts);
					
					paintLine(track, line, painter, fromX, posY, ts, clientArea);
					
					lineHeight = ts.getSize();
					addTrackPosition(track.getNumber(),posY,lineHeight);
					
					float emptyX = (this.marginLeft + fromX + line.tempWith + 2);
					float emptyWith = ( this.maximumWidth - emptyX );
					if((emptyWith - 20) > 0 && (line.lastIndex + 1) >= measureCount){
						if(emptyX < (clientArea.getX() + clientArea.getWidth())){
							emptyX = (emptyX < clientArea.getX() ? clientArea.getX() :emptyX);
							emptyWith = ( emptyWith > clientArea.getWidth() ? clientArea.getWidth() : emptyWith );
							paintLines(track,ts,painter, emptyX ,posY, emptyWith);
						}
					}
					
					float lineHeightWithSpacing = Math.round(lineHeight + getTrackSpacing() + 0.5f);
					
					posY += lineHeightWithSpacing;
					height += lineHeightWithSpacing;
				}
			}
			if(line != null){
				nextMeasureIndex = line.lastIndex + 1;
			}
		}
		
		this.setHeight(height);
		this.setWidth( getWidth() + this.marginRight );
	}
	
	public void paintLine(TGTrackImpl track,TempLine line,TGPainter painter, float fromX, float fromY,TGTrackSpacing ts,TGRectangle clientArea) {
		float posX = Math.round(this.marginLeft + fromX);
		float posY = Math.round(fromY);
		float width = this.marginLeft;
		
		//verifico si esta en el area de cliente
		boolean isAtY = (posY + ts.getSize() > clientArea.getY() && posY < clientArea.getY() + clientArea.getHeight() + (getScale() * 80f));
		
		float defaultMeasureSpacing = 0;
		if( line.fullLine ){
			float diff = ( this.maximumWidth - line.tempWith);
			if( diff != 0 && line.measures.size() > 0 ){
				defaultMeasureSpacing = (diff / line.measures.size());
			}
		}
		
		float measureSpacing = defaultMeasureSpacing;
		
		for(int i = 0;i < line.measures.size();i ++){
			int index = ((Integer)line.measures.get(i)).intValue();
			TGMeasureImpl currMeasure = (TGMeasureImpl)track.getMeasure(index);
			
			//asigno la posicion dentro del compas
			currMeasure.setPosX(posX);
			currMeasure.setPosY(posY);
			currMeasure.setTs(ts);
			
			((TGLyricImpl)track.getLyrics()).setCurrentMeasure(currMeasure);
			
			currMeasure.setFirstOfLine(i == 0);
			
			float measureWidth = currMeasure.getWidth(this);
			float measureWidthWithSpacing = (this.isBufferEnabled() ? Math.round(measureWidth + measureSpacing) : (measureWidth + measureSpacing));
			float measureSpacingAfterRound = (measureWidthWithSpacing - measureWidth);
			
			boolean isAtX = ( posX + measureWidthWithSpacing > clientArea.getX() && posX < clientArea.getX() + clientArea.getWidth());
			if(isAtX && isAtY){
				paintMeasure(currMeasure, painter, measureSpacingAfterRound);
				((TGLyricImpl)track.getLyrics()).paintCurrentNoteBeats(painter, this, currMeasure, posX, posY);
			}else{
				currMeasure.setOutOfBounds(true);
			}
			
			posX += measureWidthWithSpacing;
			width += measureWidthWithSpacing;
			measureSpacing = (defaultMeasureSpacing + (measureSpacing - measureSpacingAfterRound));
		}
		this.setWidth(Math.max(getWidth(), width));
	}
	
	public TempLine getTempLines(TGTrack track,int fromIndex,TGTrackSpacing ts) {
		int style = getStyle();
		
		TempLine line = new TempLine();
		line.maxY = 0;
		line.minY = 0;
		
		// Need to score extra-lines in edition mode
		if( (style & DISPLAY_TABLATURE) == 0 && (style & DISPLAY_SCORE) != 0 ){
			line.maxY = ((getScoreLineSpacing() * 4) + (getScoreLineSpacing() * 4));
			line.minY = -(getScoreLineSpacing() * 3);
		}
		
		int measureCount = track.countMeasures();
		for (int measureIdx = fromIndex; measureIdx < measureCount; measureIdx++) {
			TGMeasureImpl measure = (TGMeasureImpl)track.getMeasure(measureIdx);
			
			//verifico si tengo que bajar de linea
			if((line.tempWith + measure.getWidth(this)) >= this.maximumWidth ){
				if( line.measures.isEmpty() ) {
					this.addToTempLine(line, ts, measure, measureIdx);
				}
				line.fullLine = true;
				return line;
			}
			
			this.addToTempLine(line, ts, measure, measureIdx);
		}
		
		return line;
	}
	
	public void addToTempLine(TempLine line, TGTrackSpacing ts, TGMeasureImpl measure, int measureIdx) {
		line.tempWith +=  measure.getWidth(this);
		line.maxY = (measure.getMaxY() > line.maxY)?measure.getMaxY():line.maxY;
		line.minY = (measure.getMinY() < line.minY)?measure.getMinY():line.minY;
		
		line.addMeasure(measureIdx);
		measure.registerSpacing(this,ts);
	}
	
	private class TempLine{
		
		protected float tempWith;
		protected int lastIndex;
		protected boolean fullLine;
		protected float maxY = 0;
		protected float minY = 0;
		protected List<Integer> measures;
		
		public TempLine(){
			this.measures = new ArrayList<Integer>();
		}
		
		protected void addMeasure(int index){
			this.measures.add(new Integer(index));
			this.lastIndex = index;
		}
	}
}
