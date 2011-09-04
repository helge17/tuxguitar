/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.help;

import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.app.help.doc.TGDocumentation;
import org.herac.tuxguitar.app.util.MessageDialog;

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
	
	protected int execute(ActionData actionData){
		try {
			new TGDocumentation().display();
		} catch (Throwable throwable) {
			MessageDialog.errorMessage(throwable);
		}
		return 0;
	}
}
