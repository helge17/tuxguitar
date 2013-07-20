/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGLayout;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetTablatureEnabledAction extends TGActionBase{
	
	public static final String NAME = "action.view.layout-set-tablature-enabled";
	
	public SetTablatureEnabledAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		TGLayout layout = getEditor().getTablature().getViewLayout();
		layout.setStyle( ( layout.getStyle() ^ TGLayout.DISPLAY_TABLATURE ) );
		if((layout.getStyle() & TGLayout.DISPLAY_TABLATURE) == 0 && (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0 ){
			layout.setStyle( ( layout.getStyle() ^ TGLayout.DISPLAY_SCORE ) );
		}
		updateTablature();
	}
}
