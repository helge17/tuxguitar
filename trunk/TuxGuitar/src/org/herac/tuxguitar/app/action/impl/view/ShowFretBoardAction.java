/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.view;

import org.eclipse.swt.SWT;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ShowFretBoardAction extends TGActionBase{
	
	public static final String NAME = "action.view.show-fretboard";
	
	public ShowFretBoardAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		TuxGuitar.getInstance().loadCursor(SWT.CURSOR_WAIT);
		if(TuxGuitar.getInstance().getFretBoardEditor().isVisible()){
			TuxGuitar.getInstance().getFretBoardEditor().hideFretBoard();
		}else{
			TuxGuitar.getInstance().getFretBoardEditor().showFretBoard();
		}
		TuxGuitar.getInstance().loadCursor(SWT.CURSOR_ARROW);
	}
}
