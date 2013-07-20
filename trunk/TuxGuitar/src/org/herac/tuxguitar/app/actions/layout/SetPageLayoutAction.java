/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.editors.tab.Tablature;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetPageLayoutAction extends TGActionBase{
	
	public static final String NAME = "action.view.layout-set-page";
	
	public SetPageLayoutAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		Tablature tablature = getEditor().getTablature();
		tablature.setViewLayout(new TGLayoutVertical(tablature,tablature.getViewLayout().getStyle()));
		updateTablature();
	}
}
