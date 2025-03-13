package app.tuxguitar.app.action.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.view.TGToggleViewAction;
import app.tuxguitar.app.view.dialog.lyric.TGLyricEditorController;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGToggleLyricEditorAction extends TGActionBase{

	public static final String NAME = "action.gui.toggle-lyric-editor";

	public TGToggleLyricEditorAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext){
		tgActionContext.setAttribute(TGToggleViewAction.ATTRIBUTE_CONTROLLER, new TGLyricEditorController());
		TGActionManager.getInstance(getContext()).execute(TGToggleViewAction.NAME, tgActionContext);
	}
}
