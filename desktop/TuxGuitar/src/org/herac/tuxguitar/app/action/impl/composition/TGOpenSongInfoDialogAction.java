package org.herac.tuxguitar.app.action.impl.composition;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.dialog.info.TGSongInfoDialogController;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenSongInfoDialogAction extends TGActionBase{
	
	public static final String NAME = "action.gui.open-song-info-dialog";
	
	public TGOpenSongInfoDialogAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext){
		tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGSongInfoDialogController());
		TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
	}
}
