/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.help;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.app.help.about.AboutDialog;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ShowAboutAction extends Action {
	
	public static final String NAME = "action.help.about";
	
	protected Canvas imageCanvas;
	protected Image tabImage;
	
	public ShowAboutAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK);
	}
	
	protected int execute(ActionData actionData){
		new AboutDialog().open(TuxGuitar.instance().getShell());
		return 0;
	}
}
