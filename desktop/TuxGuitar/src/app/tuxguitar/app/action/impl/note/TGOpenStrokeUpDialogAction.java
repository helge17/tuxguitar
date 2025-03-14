package app.tuxguitar.app.action.impl.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.view.dialog.stroke.TGStrokeDialogController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.note.TGChangeStrokeAction;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGOpenStrokeUpDialogAction extends TGActionBase{

	public static final String NAME = "action.gui.open-stroke-up-dialog";

	public TGOpenStrokeUpDialogAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext){
		TGTrack track = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		if (!track.isPercussion()) {
			tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGStrokeDialogController());
			tgActionContext.setAttribute(TGChangeStrokeAction.ATTRIBUTE_STROKE_DIRECTION, TGStroke.STROKE_UP);
			TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
		}
	}
}
