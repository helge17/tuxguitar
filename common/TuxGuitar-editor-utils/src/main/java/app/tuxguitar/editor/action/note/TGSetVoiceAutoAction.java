package app.tuxguitar.editor.action.note;

import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

public class TGSetVoiceAutoAction extends TGSetVoiceDirectionAction {

	public static final String NAME = "action.beat.general.voice-auto";

	public TGSetVoiceAutoAction(TGContext context) {
		super(context, NAME, TGVoice.DIRECTION_NONE);
	}
}
