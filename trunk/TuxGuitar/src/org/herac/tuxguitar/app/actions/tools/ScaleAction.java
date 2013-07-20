/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.tools;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.actions.TGActionBase;
import org.herac.tuxguitar.app.tools.scale.ScaleEditor;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ScaleAction extends TGActionBase{
	
	public static final String NAME = "action.tools.scale";
	
	public ScaleAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE);
	}
	
	protected void processAction(TGActionContext context){
		new ScaleEditor().show();
	}
}
