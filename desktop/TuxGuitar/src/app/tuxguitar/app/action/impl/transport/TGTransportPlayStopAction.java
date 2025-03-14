package app.tuxguitar.app.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

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
