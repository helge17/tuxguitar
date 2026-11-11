package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGVoiceImpl;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

/**
 * Sets one stem direction on every voice of the caret voice's beat group, when the caret voice has notes and a beat
 * group.
 */
public abstract class TGSetVoiceDirectionAction extends TGActionBase {

	private final int direction;

	public TGSetVoiceDirectionAction(TGContext context, String name, int direction) {
		super(context, name);
		this.direction = direction;
	}

	protected void processAction(TGActionContext context){
		TGVoiceImpl voice = (TGVoiceImpl) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		if( voice != null && !voice.isEmpty() && !voice.isRestVoice() && voice.getBeatGroup() != null ){
			for( TGVoice current : voice.getBeatGroup().getVoices() ){
				getSongManager(context).getMeasureManager().changeVoiceDirection(current, this.direction);
			}
		}
	}
}
