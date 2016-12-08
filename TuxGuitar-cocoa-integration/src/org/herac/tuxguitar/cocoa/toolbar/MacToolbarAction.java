package org.herac.tuxguitar.cocoa.toolbar;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGToggleMainToolbarAction;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

public class MacToolbarAction {
	
	protected static void toogleToolbar(){
		new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGToggleMainToolbarAction.NAME).process();
	}
}
