package app.tuxguitar.editor.action.effect;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGChangeFadeInAction extends TGActionBase {

	public static final String NAME = "action.note.effect.change-fade-in";

	public TGChangeFadeInAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);

		if (noteRange!=null && !noteRange.isEmpty() && !track.isPercussion()) {
			boolean newValue = true;
			if (noteRange.getNotes().stream().allMatch(n -> n.getEffect().isFadeIn())) {
				newValue = false;
			}
			for (TGNote note : noteRange.getNotes()) {
				getSongManager(context).getMeasureManager().setFadeIn(note, newValue);
			}
		}
	}
}
