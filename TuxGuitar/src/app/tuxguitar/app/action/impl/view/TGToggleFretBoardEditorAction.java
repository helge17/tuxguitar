package app.tuxguitar.app.action.impl.view;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditorController;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGToggleFretBoardEditorAction extends TGActionBase{

	public static final String NAME = "action.gui.toggle-fretboard-editor";

	public TGToggleFretBoardEditorAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext){
		tgActionContext.setAttribute(TGToggleViewAction.ATTRIBUTE_CONTROLLER, new TGFretBoardEditorController());
		TGActionManager.getInstance(getContext()).execute(TGToggleViewAction.NAME, tgActionContext);
	}
}
