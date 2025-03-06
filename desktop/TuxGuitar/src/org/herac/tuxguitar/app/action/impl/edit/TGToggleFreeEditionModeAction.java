package org.herac.tuxguitar.app.action.impl.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

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
