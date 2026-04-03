package app.tuxguitar.app.action.impl.view;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.view.dialog.piano.TGPianoEditorController;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGTogglePianoEditorAction extends TGActionBase{

	public static final String NAME = "action.gui.toggle-piano-editor";

	public TGTogglePianoEditorAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext){
		tgActionContext.setAttribute(TGToggleViewAction.ATTRIBUTE_CONTROLLER, new TGPianoEditorController());
		TGActionManager.getInstance(getContext()).execute(TGToggleViewAction.NAME, tgActionContext);
	}
}
