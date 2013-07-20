/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.file;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.action.TGActionLock;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ExitAction extends TGActionBase {
	
	public static final String NAME = "action.file.exit";
	
	public ExitAction() {
		super(NAME, AUTO_LOCK | KEY_BINDING_AVAILABLE );
	}
	
	protected void processAction(TGActionContext context){
		TGActionLock.unlock();
		TuxGuitar.instance().getShell().close();
	}
}
