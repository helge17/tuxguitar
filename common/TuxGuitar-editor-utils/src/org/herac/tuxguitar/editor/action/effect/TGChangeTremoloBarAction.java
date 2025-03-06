package org.herac.tuxguitar.editor.action.effect;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public class TGChangeTremoloBarAction extends TGActionBase {

	public static final String NAME = "action.note.effect.change-tremolo-bar";

	public static final String ATTRIBUTE_EFFECT = TGEffectTremoloBar.class.getName();

	public TGChangeTremoloBarAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = (TGNoteRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGEffectTremoloBar effect = (TGEffectTremoloBar) context.getAttribute(ATTRIBUTE_EFFECT);

		if ((noteRange != null) && !noteRange.isEmpty()) {
			for (TGNote note : noteRange.getNotes()) {
				getSongManager(context).getMeasureManager().setTremoloBar(note, effect);
			}
		}
	}
}
