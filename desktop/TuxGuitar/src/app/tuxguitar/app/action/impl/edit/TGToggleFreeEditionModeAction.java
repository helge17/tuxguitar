package app.tuxguitar.app.action.impl.edit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

public class TGToggleFreeEditionModeAction extends TGActionBase {

	public static final String NAME = "action.edit.toggle-free-edition-mode";

	public TGToggleFreeEditionModeAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext context) {
		TGSongManager songManager = (TGSongManager)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGMeasure measure = (TGMeasure)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		if (songManager.getMeasureManager().isMeasureValid(measure) ) {
			songManager.toggleFreeEditionMode();
		}
	}

}
