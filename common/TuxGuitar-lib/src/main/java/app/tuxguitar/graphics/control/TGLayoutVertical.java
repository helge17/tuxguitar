/*
 * Created on 04-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;

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

	public void paintSong(UIPainter painter,UIRectangle clientArea, float fromX, float fromY) {
		this.marginLeft = getFirstMeasureSpacing();
		this.marginRight = 10;
		this.maximumWidth = (clientArea.getWidth() - (this.marginLeft + this.marginRight));
		this.setHeight(0);
		this.setWidth(0);
		this.clearTrackPositions();
		this.measureLines = new ArrayList<Integer>();

		LinePosition position = new LinePosition(Math.round(fromY + getFirstTrackSpacing()), getFirstTrackSpacing());
		int measureCount = getSong().countMeasureHeaders();
		int nextMeasureIndex = 0;
		while(measureCount > nextMeasureIndex){
			nextMeasureIndex = paintTrackLines(painter, clientArea, fromX, nextMeasureIndex, position);
		}

		this.setHeight(position.height);
		this.setWidth( getWidth() + this.marginRight );
		this.updateTimeToScroll();
	}

	// one line of each shown track, from the given measure on; returns the measure the next line starts with
	// (that of the last track: when no track is shown, it is the same measure again)
	private int paintTrackLines(UIPainter painter, UIRectangle clientArea, float fromX, int fromIndex, LinePosition position){
		int nextIndex = fromIndex;
		int number = getComponent().getTrackSelection();
		Iterator<TGTrack> tracks = getSong().getTracks();
		while(tracks.hasNext()){
			TGTrackImpl track = (TGTrackImpl) tracks.next();
			if(number < 0 || track.getNumber() == number){
				nextIndex = (paintTrackLine(track, painter, clientArea, fromX, fromIndex, position).lastIndex + 1);
			}
		}
		return nextIndex;
	}

	private TempLine paintTrackLine(TGTrackImpl track, UIPainter painter, UIRectangle clientArea, float fromX, int fromIndex, LinePosition position){
		TGTrackSpacing ts = new TGTrackSpacing(this) ;
		if(fromIndex == 0){
			((TGLyricImpl)track.getLyrics()).start();
		}
		TempLine line = getTempLines(track, fromIndex, ts);
		setStaffSpacing(ts, track, line.minY, line.maxY);

		paintLine(track, line, painter, fromX, position.posY, ts, clientArea);

		float lineHeight = ts.getSize();
		addTrackPosition(track.getNumber(), position.posY, lineHeight);
		paintStaffAfterSong(track, line, ts, painter, fromX, position.posY, clientArea);
		registerMeasureLines(line, position.lineNumber);
		position.next(Math.round(lineHeight + getTrackSpacing() + 0.5f));
		return line;
	}

	// on the line holding the last measure, the empty staff runs on to the right margin (within the client area)
	private void paintStaffAfterSong(TGTrackImpl track, TempLine line, TGTrackSpacing ts, UIPainter painter, float fromX, float posY, UIRectangle clientArea){
		float emptyX = (this.marginLeft + fromX + line.tempWith + 2);
		float emptyWith = ( this.maximumWidth - emptyX );
		boolean lastLine = ((line.lastIndex + 1) >= getSong().countMeasureHeaders());
		if( lastLine && (emptyWith - 20) > 0 && emptyX < (clientArea.getX() + clientArea.getWidth()) ){
			paintLines(track, ts, painter, Math.max(emptyX, clientArea.getX()), posY, Math.min(emptyWith, clientArea.getWidth()));
		}
	}

	private void registerMeasureLines(TempLine line, int lineNumber){
		for (int mIndex : line.measures) {
			if ((mIndex>=this.measureLines.size()) || (this.measureLines.get(mIndex) == 0)) {
				this.measureLines.add(mIndex, lineNumber);
			}
		}
	}

	/** Where the next staff line goes: its y, the height of the song so far, and its number. */
	private static class LinePosition {

		private float posY;
		private float height;
		private int lineNumber;

		LinePosition(float posY, float height){
			this.posY = posY;
			this.height = height;
		}

		void next(float lineHeightWithSpacing){
			this.posY += lineHeightWithSpacing;
			this.height += lineHeightWithSpacing;
			this.lineNumber ++;
		}
	}

	private void updateTimeToScroll() {
		this.timeToNextScrollMs.clear();
		
		TGTrackImpl track = null;
		int number = getComponent().getTrackSelection();
		Iterator<TGTrack> tracks = getSong().getTracks();
		while(tracks.hasNext()){
			TGTrackImpl nextTrack = (TGTrackImpl) tracks.next();
			if(number < 0 || nextTrack.getNumber() == number){
				track = nextTrack;
			}
		}
		if (track == null) return;
		int measureCount = getSong().countMeasureHeaders();
		float lastLineY = ((TGMeasureImpl)track.getMeasure(measureCount-1)).getPosY();
		for (int measIndex = measureCount-1; measIndex>=0; measIndex--) {
			this.timeToNextScrollMs.add(0,0);
			TGMeasureImpl measure = (TGMeasureImpl)track.getMeasure(measIndex);
			if (measure.getPosY() != lastLineY) {	// no need to scroll at last line
				// duration in milliseconds of measure (at nominal tempo)
				if (measure.getPosY() != ((TGMeasureImpl)track.getMeasure(measIndex+1)).getPosY()) {
					// last measure in line, time to scroll = measure duration
					this.timeToNextScrollMs.set(0, measure.getHeader().getDurationInMs());
				}
				else {
					this.timeToNextScrollMs.set(0, this.timeToNextScrollMs.get(1) + measure.getHeader().getDurationInMs());
				}
			}
		}
	}

	public void paintLine(TGTrackImpl track,TempLine line,UIPainter painter, float fromX, float fromY,TGTrackSpacing ts,UIRectangle clientArea) {
		float posX = Math.round(this.marginLeft + fromX);
		float posY = Math.round(fromY);
		float width = this.marginLeft;

		//verifico si esta en el area de cliente
		boolean isAtY = (posY + ts.getSize() > clientArea.getY() && posY < clientArea.getY() + clientArea.getHeight() + (getScale() * 80f));

		float defaultMeasureSpacing = getDefaultMeasureSpacing(line);
		float measureSpacing = defaultMeasureSpacing;

		for(int i = 0;i < line.measures.size();i ++){
			TGMeasureImpl currMeasure = placeMeasure(track, line, i, posX, posY, ts);

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

	// a full line spreads what is left of the maximum width evenly over its measures
	private float getDefaultMeasureSpacing(TempLine line) {
		if( line.fullLine && line.measures.size() > 0 ){
			return (( this.maximumWidth - line.tempWith) / line.measures.size());
		}
		return 0;
	}

	// the i-th measure of the line, placed at the given position, its lyrics counted
	private TGMeasureImpl placeMeasure(TGTrackImpl track, TempLine line, int i, float posX, float posY, TGTrackSpacing ts) {
		TGMeasureImpl measure = (TGMeasureImpl)track.getMeasure(line.measures.get(i).intValue());
		measure.setPosX(posX);
		measure.setPosY(posY);
		measure.setTs(ts);

		((TGLyricImpl)track.getLyrics()).setCurrentMeasure(measure);

		measure.setFirstOfLine(i == 0);
		measure.setLastOfLine(i == line.measures.size()-1);
		return measure;
	}

	public TempLine getTempLines(TGTrack track,int fromIndex,TGTrackSpacing ts) {
		TempLine line = new TempLine();
		line.maxY = getStaffMaxY();
		line.minY = getStaffMinY();

		int measureCount = track.countMeasures();
		boolean lineBreak = false;
		for (int measureIdx = fromIndex; measureIdx < measureCount; measureIdx++) {
			TGMeasureImpl measure = (TGMeasureImpl)track.getMeasure(measureIdx);

			//verifico si tengo que bajar de linea
			line.fullLine = (line.tempWith + measure.getWidth(this)) >= this.maximumWidth;
			if(line.fullLine || lineBreak){
				if( line.measures.isEmpty() ) {
					this.addToTempLine(line, ts, measure, measureIdx);
				}
				return line;
			}

			this.addToTempLine(line, ts, measure, measureIdx);
			if (measureIdx+1<measureCount) lineBreak = ((TGMeasureImpl)track.getMeasure(measureIdx+1)).isLineBreak();
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
			this.measures.add(Integer.valueOf(index));
			this.lastIndex = index;
		}
	}
}
