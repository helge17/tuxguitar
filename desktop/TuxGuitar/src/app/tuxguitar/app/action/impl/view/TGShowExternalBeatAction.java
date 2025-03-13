package app.tuxguitar.app.action.impl.view;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.editor.TGExternalBeatViewerManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.util.TGContext;

public class TGShowExternalBeatAction extends TGActionBase {

	public static final String NAME = "action.gui.show-external-beat";

	public TGShowExternalBeatAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);

		TGExternalBeatViewerManager.getInstance(getContext()).showExternalBeat(beat, context);
	}
}
