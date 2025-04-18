package app.tuxguitar.app.action.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.view.dialog.track.TGTrackTuningDialogController;
import app.tuxguitar.app.view.dialog.track.TGTrackStringCountDialogController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGOpenTrackTuningDialogAction extends TGActionBase{

	public static final String NAME = "action.gui.open-track-tuning-dialog";

	public TGOpenTrackTuningDialogAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext) {
		TGTrack track = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);

		if( track.isPercussion()) {
			tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGTrackStringCountDialogController());
			TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
		} else {
			tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGTrackTuningDialogController());
			TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
		}
	}
}