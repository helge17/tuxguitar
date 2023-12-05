package org.herac.tuxguitar.app.action.impl.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.dialog.stroke.TGStrokeDialogController;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.note.TGChangeStrokeAction;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenStrokeUpDialogAction extends TGActionBase{
	
	public static final String NAME = "action.gui.open-stroke-up-dialog";
	
	public TGOpenStrokeUpDialogAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext){
		tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGStrokeDialogController());
		tgActionContext.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_DIRECTION, TGStroke.STROKE_UP);
		TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
	}
}
