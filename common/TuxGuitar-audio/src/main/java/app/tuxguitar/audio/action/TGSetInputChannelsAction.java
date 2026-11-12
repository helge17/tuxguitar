package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;

/**
 * Sets the input channel(s) of the audio device that an audio channel records from: one for mono takes, a pair for
 * stereo ones (any two different channels, in either order). They are part of the song: the change is undone like any
 * channel change. Refused on a channel that plays notes, and for a pair of one channel.
 */
public class TGSetInputChannelsAction extends TGActionBase {

	public static final String NAME = "action.audio.channel.set-input-channels";

	// the input channels as picked (int[]): one, or a pair, left then right
	public static final String ATTRIBUTE_INPUT_CHANNELS = "inputChannels";

	public TGSetInputChannelsAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGChannel channel = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL);
		TGAudioInput input = TGAudioInput.of(context.<int[]>getAttribute(ATTRIBUTE_INPUT_CHANNELS));
		if (channel.isAudioChannel() && input != null) {
			channel.setAudioInput(input);
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
}
