/*
 * Created on 04-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.tab.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.TGLyricImpl;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.gui.editors.tab.TGTrackSpacing;
import org.herac.tuxguitar.gui.editors.tab.Tablature;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PageViewLayout extends ViewLayout{
	
	private static final int STATIC_WIDTH = TuxGuitar.instance().getConfig().getIntConfigValue(TGConfigKeys.LAYOUT_PAGE_FORCE_WIDTH, 0);
	
	private int maximumWidth;
	private int marginLeft;
	private int marginRight;
	
	public PageViewLayout(Tablature tablature,int style){
		super(tablature,style);
	}
	
	public int getMode(){
		return MODE_PAGE;
	}
	
	public void paintSong(TGPainter painter,Rectangle clientArea,int fromX,int fromY) {
		this.maximumWidth = STATIC_WIDTH;
		this.marginLeft = getFirstMeasureSpacing();
		this.marginRight = 10;
		this.setWidth(0);
		this.setHeight(0);
		this.clearTrackPositions();
		
		int style = getStyle();
		int number = ((style & ViewLayout.DISPLAY_MULTITRACK) == 0?getTablature().getCaret().getTrack().getNumber():-1);
		int posY = fromY + getFirstTrackSpacing();
		int height = getFirstTrackSpacing();
		int lineHeight = 0;
		
		int measureCount = getSongManager().getSong().countMeasureHeaders();
		int nextMeasureIndex = 0;
		while(measureCount > nextMeasureIndex){
			TempLine line = null;
			Iterator tracks = getSongManager().getSong().getTracks();
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
						if(line.maxY + getMinScoreTabSpacing() > getScoreSpacing()){
							ts.setSize(TGTrackSpacing.POSITION_SCORE_DOWN_LINES, (line.maxY - (getScoreLineSpacing() * 4)) );
						}
					}
					if((style & DISPLAY_TABLATURE) != 0){
						ts.setSize(TGTrackSpacing.POSITION_TABLATURE_TOP_SEPARATOR, ((style & DISPLAY_SCORE) != 0 ? getMinScoreTabSpacing() : Math.max(Math.abs(line.minY), getStringSpacing()) ));
						ts.setSize(TGTrackSpacing.POSITION_TABLATURE, ((style & DISPLAY_SCORE) != 0 ?  track.getTabHeight() + getStringSpacing() + 1 : Math.max( line.maxY, track.getTabHeight() + getStringSpacing() + 1) ));
					}
					ts.setSize(TGTrackSpacing.POSITION_LYRIC,10);
					checkDefaultSpacing(ts);
					
					paintLine(track,line,painter,fromX,posY,ts,clientArea);
					
					lineHeight = ts.getSize();
					addTrackPosition(track.getNumber(),posY,lineHeight);
					
					int emptyX = (this.marginLeft + fromX + line.tempWith + 2);
					int emptyWith = ( getMaxWidth() - emptyX );
					if((emptyWith - 20) > 0 && (line.lastIndex + 1) >= measureCount){
						if(emptyX < (clientArea.x + clientArea.width)){
							emptyX = (emptyX < clientArea.x ? clientArea.x :emptyX);
							emptyWith = ( emptyWith > clientArea.width ? clientArea.width : emptyWith );
							paintLines(track,ts,painter, emptyX ,posY, emptyWith);
						}
					}
					
					posY += lineHeight + getTrackSpacing();
					height += lineHeight + getTrackSpacing();
				}
			}
			if(line != null){
				nextMeasureIndex = line.lastIndex + 1;
			}
		}
		
		this.setHeight(height);
		this.setWidth( getWidth() + this.marginRight );
		this.paintCaret(painter);
	}
	
	public void paintLine(TGTrackImpl track,TempLine line,TGPainter painter,int fromX, int fromY,TGTrackSpacing ts,Rectangle clientArea) {
		int posX = (this.marginLeft + fromX);
		int posY = fromY;
		int width = this.marginLeft;
		
		//verifico si esta en el area de cliente
		boolean isAtY = (posY + ts.getSize() > clientArea.y && posY < clientArea.y + clientArea.height + 80);
		
		int measureSpacing = 0;
		if(line.fullLine){
			int diff = ( getMaxWidth() - line.tempWith);
			if(diff != 0 && line.measures.size() > 0){
				measureSpacing = diff / line.measures.size();
			}
		}
		
		for(int i = 0;i < line.measures.size();i ++){
			int index = ((Integer)line.measures.get(i)).intValue();
			TGMeasureImpl currMeasure = (TGMeasureImpl)track.getMeasure(index);
			
			//asigno la posicion dentro del compas
			currMeasure.setPosX(posX);
			currMeasure.setPosY(posY);
			currMeasure.setTs(ts);
			
			((TGLyricImpl)track.getLyrics()).setCurrentMeasure(currMeasure);
			
			currMeasure.setFirstOfLine(i == 0);
			
			int measureWidth = ( currMeasure.getWidth(this) + measureSpacing );
			boolean isAtX = ( posX + measureWidth > clientArea.x && posX < clientArea.x + clientArea.width);
			if(isAtX && isAtY){
				paintMeasure(currMeasure,painter,measureSpacing);
				((TGLyricImpl)track.getLyrics()).paintCurrentNoteBeats(painter,this,currMeasure,posX, posY);
			}else{
				currMeasure.setOutOfBounds(true);
			}
			
			posX += measureWidth;
			width += measureWidth;
		}
		this.setWidth(Math.max(getWidth(),width));
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
			if((line.tempWith + measure.getWidth(this)) >=  getMaxWidth() && !line.measures.isEmpty()){
				line.fullLine = true;
				return line;
			}
			line.tempWith +=  measure.getWidth(this);
			line.maxY = (measure.getMaxY() > line.maxY)?measure.getMaxY():line.maxY;
			line.minY = (measure.getMinY() < line.minY)?measure.getMinY():line.minY;
			
			line.addMeasure(measureIdx);
			measure.registerSpacing(this,ts);
		}
		
		return line;
	}
	
	public int getMaxWidth(){
		if(this.maximumWidth <= 0){
			int marginLeft = 0;
			int marginRight = 0;
			int monitorWidth = getTablature().getMonitor().getClientArea().width;
			Rectangle tablatureArea = getTablature().getClientArea();
			Composite parent = getTablature().getParent();
			while( parent != null ){
				Rectangle parentArea = parent.getClientArea();
				parent = parent.getParent();
				if( parent == null ){
					marginRight = ( parentArea.width - (marginLeft + tablatureArea.width ) );
				}else{
					marginLeft += parentArea.x ;
				}
			}
			this.maximumWidth = (monitorWidth - ( marginLeft + marginRight ) );
		}
		return (this.maximumWidth - (this.marginLeft + this.marginRight));
	}
	
	private class TempLine{
		protected int tempWith;
		protected int lastIndex;
		protected boolean fullLine;
		protected int maxY = 0;
		protected int minY = 0;
		protected List measures;
		
		public TempLine(){
			this.measures = new ArrayList();
		}
		
		protected void addMeasure(int index){
			this.measures.add(new Integer(index));
			this.lastIndex = index;
		}
	}
}
