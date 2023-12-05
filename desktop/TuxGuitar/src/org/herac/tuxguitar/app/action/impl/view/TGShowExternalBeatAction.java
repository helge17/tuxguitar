package org.herac.tuxguitar.app.action.impl.view;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.editor.TGExternalBeatViewerManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGContext;

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
