/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.file;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.ActionLock;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ExitAction extends Action {
	public static final String NAME = "action.file.exit";
	
	public ExitAction() {
		super(NAME, AUTO_LOCK | KEY_BINDING_AVAILABLE );
	}
	
	protected int execute(TypedEvent e){
		ActionLock.unlock();
		TuxGuitar.instance().getShell().close();
		return 0;
	}
}
