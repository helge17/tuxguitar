/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.help;

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.help.about.AboutDialog;

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
	
	protected int execute(TypedEvent e){
		new AboutDialog().open(TuxGuitar.instance().getShell());
		return 0;
	}
}
