package app.tuxguitar.editor.action.composition;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.util.TGContext;

public class TGDoubleBarAction extends TGActionBase {

	public static final String NAME = "action.toggle.double-bar";

	public TGDoubleBarAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext context) {
		TGMeasureHeader header = (TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		TGSongManager songManager = getSongManager(context);
		
		if (header.isDoubleBar() || (header.getNumber() < header.getSong().countMeasureHeaders())) {
			songManager.changeDoubleBar(header);
	
			if (header.isDoubleBar()) {
				TGActionManager actionManager = TGActionManager.getInstance(getContext());
				// conflict with repeat close?
				if (header.getRepeatClose() > 0) {
					context.setAttribute(TGRepeatCloseAction.ATTRIBUTE_REPEAT_COUNT, 0);
					actionManager.execute(TGRepeatCloseAction.NAME, context);
				}
				// conflict with repeat open in next measure?
				TGMeasureHeader nextHeader = songManager.getNextMeasureHeader(header.getSong(), header);
				if ((nextHeader != null) && nextHeader.isRepeatOpen()) {
					context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, nextHeader);
					actionManager.execute(TGRepeatOpenAction.NAME, context);
					context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
				}
			}
		}
	}

}
