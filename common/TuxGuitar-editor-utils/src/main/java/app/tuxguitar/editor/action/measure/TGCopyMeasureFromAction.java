package app.tuxguitar.editor.action.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

public class TGCopyMeasureFromAction extends TGActionBase {

	public static final String NAME = "action.measure.copy.from";

	public static final String ATTRIBUTE_FROM = "from";

	public TGCopyMeasureFromAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGMeasure from = ((TGMeasure) context.getAttribute(ATTRIBUTE_FROM));

		songManager.getTrackManager().copyMeasureFrom(measure, from);
	}
}
