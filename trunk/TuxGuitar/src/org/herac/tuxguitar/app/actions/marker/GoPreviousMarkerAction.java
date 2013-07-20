/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.marker;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.actions.TGActionBase;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.marker.MarkerNavigator;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GoPreviousMarkerAction extends TGActionBase{
	
	public static final String NAME = "action.marker.go-previous";
	
	public GoPreviousMarkerAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		Caret caret = getEditor().getTablature().getCaret();
		
		new MarkerNavigator().goToSelectedMarker(getSongManager().getPreviousMarker(caret.getMeasure().getNumber()));
	}
}
