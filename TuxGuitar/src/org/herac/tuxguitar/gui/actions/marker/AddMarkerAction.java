/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.marker;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.marker.MarkerEditor;
import org.herac.tuxguitar.gui.marker.MarkerList;
import org.herac.tuxguitar.song.models.TGMarker;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddMarkerAction extends Action{
	public static final String NAME = "action.marker.add";
	
	public AddMarkerAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		if(new MarkerEditor(getMarker()).open(getEditor().getTablature().getShell())){
			MarkerList.instance().update(true);
		}
		return 0;
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
