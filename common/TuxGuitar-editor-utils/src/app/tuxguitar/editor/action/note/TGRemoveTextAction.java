package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.util.TGContext;

public class TGRemoveTextAction extends TGActionBase {

	public static final String NAME = "action.beat.general.remove-text";

	public TGRemoveTextAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		TGSongManager tgSongManager = getSongManager(context);
		tgSongManager.getMeasureManager().removeText(beat);
	}
}
