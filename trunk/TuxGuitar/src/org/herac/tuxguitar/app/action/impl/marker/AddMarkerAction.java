/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.marker;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.marker.MarkerEditor;
import org.herac.tuxguitar.app.marker.MarkerList;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.song.models.TGMarker;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddMarkerAction extends TGActionBase{
	
	public static final String NAME = "action.marker.add";
	
	public AddMarkerAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		if(new MarkerEditor(getMarker()).open(getEditor().getTablature().getShell())){
			MarkerList.instance().update(true);
		}
	}
	
	private TGMarker getMarker(){
		TGMeasureImpl measure = getEditor().getTablature().getCaret().getMeasure();
		if (measure != null) {
			TGMarker marker = getSongManager().getMarker(measure.getNumber());
			if(marker == null){
				marker = getSongManager().getFactory().newMarker();
				marker.setMeasure(measure.getNumber());
			}
			return marker;
		}
		return null;
	}
}
