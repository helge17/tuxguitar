/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.measure;

import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.app.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.app.editors.tab.Tablature;
import org.herac.tuxguitar.song.models.TGBeat;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SelectMeasureAction extends Action{
	public static final String NAME = "action.measure.select";
	
	public SelectMeasureAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK );
	}
	
	protected int execute(AWTEvent e){
		if( e instanceof MouseEvent){
			Point p = ((MouseEvent)e).getPoint();
			Tablature tablature = getEditor().getTablature();
			if(p.x >= 0 && p.y >= 0){
				TGTrackImpl track = tablature.getCaret().getTrack();
				if (track != null) {
					TGMeasureImpl measure = findSelectedMeasure(track, p.x, p.y);
					if (measure != null) {
						TGBeat beat = tablature.getSongManager().getMeasureManager().getFirstBeat( measure.getBeats() );
						if( beat != null ){
							tablature.getCaret().moveTo(track, measure, beat,  1);
							TuxGuitar.instance().setFocus();
							return AUTO_UPDATE;
						}
					}
				}
			}
		}
		return 0;
	}
	
	public TGMeasureImpl findSelectedMeasure(TGTrackImpl track,int x,int y){
		Tablature tablature = getEditor().getTablature();
		TGMeasureImpl measure = null;
		int minorDistance = 0;
		
		Iterator it = track.getMeasures();
		while(it.hasNext()){
			TGMeasureImpl m = (TGMeasureImpl)it.next();
			if(!m.isOutOfBounds() && m.getTs() != null){
				boolean isAtX = (x >= m.getPosX() && x <= m.getPosX() + m.getWidth(tablature.getViewLayout()) + m.getSpacing());
				if(isAtX){
					int measureHeight = m.getTs().getSize();
					int distanceY = Math.min(Math.abs(y - (m.getPosY())),Math.abs(y - ( m.getPosY() + measureHeight - 10)));
					if(measure == null || distanceY < minorDistance){
						measure = m;
						minorDistance = distanceY;
					}
				}
			}
		}
		return measure;
	}
}
