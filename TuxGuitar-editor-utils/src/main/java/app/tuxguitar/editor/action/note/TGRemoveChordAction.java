package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

public class TGRemoveChordAction extends TGActionBase {

	public static final String NAME = "action.beat.general.remove-chord";

	public TGRemoveChordAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);

		if( beat.isChordBeat() ){
			songManager.getMeasureManager().removeChord(measure, beat.getStart());
		}
	}
}
