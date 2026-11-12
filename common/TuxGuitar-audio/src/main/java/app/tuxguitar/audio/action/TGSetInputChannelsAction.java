package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;

/**
 * Sets the input channel(s) of the audio device that an audio channel records from: one for mono takes, a pair for
 * stereo ones. They are part of the song: the change is undone like any channel change. Refused on a channel that
 * plays notes.
 */
public class TGSetInputChannelsAction extends TGActionBase {

	public static final String NAME = "action.audio.channel.set-input-channels";

	public static final String ATTRIBUTE_INPUT = "audioInput";

	public TGSetInputChannelsAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGChannel channel = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL);
		TGAudioInput input = context.getAttribute(ATTRIBUTE_INPUT);
		if (channel.isAudioChannel()) {
			channel.setAudioInput(input);
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
}
