package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TGCleanBeatAction extends TGActionBase {

	public static final String NAME = "action.note.general.clean-beat";

	public TGCleanBeatAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGBeatRange beats = (TGBeatRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);

		if (beats!=null && !beats.isEmpty()){
			for (TGBeat beat : beats.getBeats()) {
				getSongManager(context).getMeasureManager().cleanBeat(beat);
			}
		} else {
			TGBeat beat = (TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
			if( beat != null ){
				getSongManager(context).getMeasureManager().cleanBeat(beat);
			}
		}
	}
}
