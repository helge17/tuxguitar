package app.tuxguitar.editor.action.effect;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGChangeSlideNoteAction extends TGActionBase {

	public static final String NAME = "action.note.effect.change-slide";

	public TGChangeSlideNoteAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);

		if (noteRange!=null && !noteRange.isEmpty() && !track.isPercussion()) {
			boolean newValue = true;
			if (noteRange.getNotes().stream().allMatch(n -> n.getEffect().isSlide())) {
				newValue = false;
			}
			for (TGNote note : noteRange.getNotes()) {
				getSongManager(context).getMeasureManager().setSlideNote(note, newValue);
			}
		}
	}
}
