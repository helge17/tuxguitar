package app.tuxguitar.editor.action.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

public class TGCleanMeasureAction extends TGActionBase {

	public static final String NAME = "action.measure.clean";

	public TGCleanMeasureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGMeasure tgMeasure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGSongManager tgSongManager = getSongManager(context);
		tgSongManager.getMeasureManager().cleanMeasure(tgMeasure);
	}
}
