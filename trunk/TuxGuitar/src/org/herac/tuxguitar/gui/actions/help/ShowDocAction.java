/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.help;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.help.doc.TGDocumentation;
import org.herac.tuxguitar.gui.util.MessageDialog;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ShowDocAction extends Action {
	public static final String NAME = "action.help.doc";
	
	public ShowDocAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | KEY_BINDING_AVAILABLE );
	}
	
	protected int execute(TypedEvent e){
		try {
			new TGDocumentation().display();
		} catch (Throwable throwable) {
			MessageDialog.errorMessage(throwable);
		}
		return 0;
	}
}
