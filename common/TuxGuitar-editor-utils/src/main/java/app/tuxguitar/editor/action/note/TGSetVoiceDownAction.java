package app.tuxguitar.editor.action.note;

import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

public class TGSetVoiceDownAction extends TGSetVoiceDirectionAction {

	public static final String NAME = "action.beat.general.voice-down";

	public TGSetVoiceDownAction(TGContext context) {
		super(context, NAME, TGVoice.DIRECTION_DOWN);
	}
}
