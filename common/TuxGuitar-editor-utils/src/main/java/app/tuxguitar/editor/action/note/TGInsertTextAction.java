package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGText;
import app.tuxguitar.util.TGContext;

public class TGInsertTextAction extends TGActionBase {

	public static final String NAME = "action.beat.general.insert-text";

	public static final String ATTRIBUTE_TEXT_VALUE = "textValue";

	public TGInsertTextAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		String textValue = ((String) context.getAttribute(ATTRIBUTE_TEXT_VALUE));

		if( beat != null && textValue != null ){
			TGSongManager tgSongManager = getSongManager(context);
			TGText tgText = getSongManager(context).getFactory().newText();
			tgText.setValue(textValue);

			tgSongManager.getMeasureManager().addText(beat, tgText);
		}
	}
}
