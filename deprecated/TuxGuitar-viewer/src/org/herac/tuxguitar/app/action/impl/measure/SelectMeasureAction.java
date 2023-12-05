package org.herac.tuxguitar.app.action.impl.measure;

import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.tab.Tablature;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGContext;

public class SelectMeasureAction extends TGActionBase {
	
	public static final String NAME = "action.measure.select";
	
	public SelectMeasureAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		AWTEvent awtEvent = context.getAttribute(AWTEvent.class.getName());
		if( awtEvent instanceof MouseEvent){
			Point p = ((MouseEvent)awtEvent).getPoint();
			Tablature tablature = TuxGuitar.instance().getTablatureEditor().getTablature();
			if(p.x >= 0 && p.y >= 0){
				TGTrackImpl track = tablature.getCaret().getTrack();
				if (track != null) {
					TGMeasureImpl measure = findSelectedMeasure(track, p.x, p.y);
					if (measure != null) {
						TGBeat beat = tablature.getSongManager().getMeasureManager().getFirstBeat( measure.getBeats() );
						if( beat != null ){
							tablature.getCaret().moveTo(track, measure, beat,  1);
							TuxGuitar.instance().setFocus();
						}
					}
				}
			}
		}
	}
	
	public TGMeasureImpl findSelectedMeasure(TGTrackImpl track, float x, float y){
		Tablature tablature = TuxGuitar.instance().getTablatureEditor().getTablature();
		TGMeasureImpl measure = null;
		float minorDistance = 0;
		
		Iterator<?> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasureImpl m = (TGMeasureImpl)it.next();
			if(!m.isOutOfBounds() && m.getTs() != null){
				boolean isAtX = (x >= m.getPosX() && x <= m.getPosX() + m.getWidth(tablature.getViewLayout()) + m.getSpacing());
				if(isAtX){
					float measureHeight = m.getTs().getSize();
					float distanceY = Math.min(Math.abs(y - (m.getPosY())),Math.abs(y - ( m.getPosY() + measureHeight - 10)));
					if( measure == null || distanceY < minorDistance ){
						measure = m;
						minorDistance = distanceY;
					}
				}
			}
		}
		return measure;
	}
}
