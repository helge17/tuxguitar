/*
 * Created on 04-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.tuxguitar.graphics.control;

import java.util.Iterator;

import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGLayoutHorizontal extends TGLayout{

	public TGLayoutHorizontal(TGController controller,int style){
		super(controller,style);
	}

	public int getMode(){
		return MODE_HORIZONTAL;
	}

	public void paintSong(UIPainter painter,UIRectangle clientArea, float fromX, float fromY) {
		this.setWidth(0);
		this.setHeight(0);
		this.clearTrackPositions();

		int number = getComponent().getTrackSelection();
		float posY = Math.round(fromY + getFirstTrackSpacing());
		float height = getFirstTrackSpacing();
		Iterator<TGTrack> tracks = getSong().getTracks();
		while(tracks.hasNext()){
			TGTrackImpl track = (TGTrackImpl) tracks.next();
			if(number < 0 || track.getNumber() == number){
				float trackHeightWithSpacing = paintTrack(track, painter, clientArea, fromX, posY);
				posY += trackHeightWithSpacing;
				height += trackHeightWithSpacing;
			}
		}
		if(getWidth() > clientArea.getWidth()){
			// solo para dar un espacio.
			this.setWidth( getWidth() + getFirstMeasureSpacing());
		}
		this.setHeight(height);
		this.updateTimeToScroll();
	}

	// the whole track on one line; returns the height it takes, with the spacing to the next track
	private float paintTrack(TGTrackImpl track, UIPainter painter, UIRectangle clientArea, float fromX, float posY){
		TGTrackSpacing ts = new TGTrackSpacing(this) ;
		((TGLyricImpl)track.getLyrics()).start();

		float maxY = getStaffMaxY();
		float minY = getStaffMinY();
		Iterator<TGMeasure> measures = track.getMeasures();
		while(measures.hasNext()){
			TGMeasureImpl measure = (TGMeasureImpl)measures.next();
			maxY = Math.max(measure.getMaxY(), maxY);
			minY = Math.min(measure.getMinY(), minY);
			measure.registerSpacing(this,ts);
		}
		setStaffSpacing(ts, track, minY, maxY);

		paintMeasures(track,painter,fromX,posY,ts,clientArea);
		paintLines(track,ts,painter,fromX + (getWidth() + (2f * getScale())),posY, (clientArea.getWidth() - (fromX + getWidth()) ));

		float trackHeight = ts.getSize();
		addTrackPosition(track.getNumber(),posY,trackHeight);
		return Math.round(trackHeight + getTrackSpacing() + 0.5f);
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
		for (int measIndex = measureCount-1; measIndex>=0; measIndex--) {
			this.timeToNextScrollMs.add(0,0);
			TGMeasureImpl measure = (TGMeasureImpl)track.getMeasure(measIndex);
			this.timeToNextScrollMs.set(0, measure.getHeader().getDurationInMs());
		}
	}

	public void paintMeasures(TGTrackImpl track,UIPainter painter, float fromX, float fromY,TGTrackSpacing ts,UIRectangle clientArea) {
		float posX = Math.round(fromX + getFirstMeasureSpacing());
		float posY = fromY;
		float width = getFirstMeasureSpacing();

		Iterator<TGMeasure> measures = track.getMeasures();
		while(measures.hasNext()){
			TGMeasureImpl measure = (TGMeasureImpl)measures.next();

			//asigno la posicion dentro del compas
			measure.setPosX(posX);
			measure.setPosY(posY);
			measure.setTs(ts);

			((TGLyricImpl)track.getLyrics()).setCurrentMeasure(measure);

			float measureWidth = measure.getWidth(this);
			float measureWidthRounded = (this.isBufferEnabled() ? Math.round(measureWidth) : measureWidth);
			float measureSpacing = (measureWidthRounded - measureWidth);

			//Solo pinto lo que entre en pantalla
			boolean isAtX = ((posX + measure.getWidth(this)) > clientArea.getX() - 100 && posX < clientArea.getX() + clientArea.getWidth() + measure.getWidth(this) + 100);
			boolean isAtY = (posY + ts.getSize() > clientArea.getY() && posY < clientArea.getY() + clientArea.getHeight() + 80);
			if(isAtX && isAtY){
				paintMeasure(measure, painter, measureSpacing);
				((TGLyricImpl)track.getLyrics()).paintCurrentNoteBeats(painter,this,measure,posX, posY);
			}else{
				measure.setOutOfBounds(true);
			}

			posX += measureWidthRounded;
			width += measureWidthRounded;
		}
		this.setWidth(Math.max(getWidth(),width));
	}
}
