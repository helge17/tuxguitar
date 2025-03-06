package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;

public class TGTransportPlayStopAction extends TGActionBase {

	public static final String NAME = "action.transport.playStop";

	public TGTransportPlayStopAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGBeatRange beatRange = (TGBeatRange)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		boolean isSelectionActive = Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SELECTION_IS_ACTIVE));

		TGTransport.getInstance(getContext()).playStop(beatRange.getBeats().get(0),
				isSelectionActive ? beatRange.getBeats().get(beatRange.getBeats().size()-1) : null);
	}
}
