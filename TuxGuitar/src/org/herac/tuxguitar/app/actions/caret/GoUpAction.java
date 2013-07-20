/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.caret;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.action.TGActionBase;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GoUpAction extends TGActionBase{
	
	public static final String NAME = "action.caret.go-up";
	
	public GoUpAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | DISABLE_ON_PLAYING | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		getEditor().getTablature().getCaret().moveUp();
	}
}
