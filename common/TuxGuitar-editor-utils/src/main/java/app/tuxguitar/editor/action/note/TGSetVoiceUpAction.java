package app.tuxguitar.editor.action.note;

import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

public class TGSetVoiceUpAction extends TGSetVoiceDirectionAction {

	public static final String NAME = "action.beat.general.voice-up";

	public TGSetVoiceUpAction(TGContext context) {
		super(context, NAME, TGVoice.DIRECTION_UP);
	}
}
