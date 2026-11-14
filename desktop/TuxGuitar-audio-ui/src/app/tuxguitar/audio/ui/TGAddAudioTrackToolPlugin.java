package app.tuxguitar.audio.ui;

import app.tuxguitar.audio.action.TGAddAudioTrackAction;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.util.TGContext;

/**
 * Tools > Add Audio Track: a new audio track on input channel 1 (undoable).
 */
public class TGAddAudioTrackToolPlugin extends TGAudioToolPlugin {

	public TGAddAudioTrackToolPlugin() {
		super("audio.add-track");
	}

	protected void doAction(TGContext context) {
		TGActionProcessor processor = new TGActionProcessor(context, TGAddAudioTrackAction.NAME);
		processor.setAttribute(TGAddAudioTrackAction.ATTRIBUTE_INPUT_CHANNEL, 1);
		processor.process();
	}
}
