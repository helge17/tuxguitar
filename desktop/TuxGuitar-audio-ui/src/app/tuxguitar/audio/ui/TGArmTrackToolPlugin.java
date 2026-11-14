package app.tuxguitar.audio.ui;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.audio.action.TGArmTrackAction;
import app.tuxguitar.audio.action.TGDisarmTrackAction;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.util.TGContext;

/**
 * Tools > Arm / Disarm Track: the caret's track is armed for recording, or disarmed when it is.
 */
public class TGArmTrackToolPlugin extends TGAudioToolPlugin {

	public TGArmTrackToolPlugin() {
		super("audio.arm");
	}

	protected void doAction(TGContext context) {
		boolean armed = TGAudioSession.getInstance(context).isArmed(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack());
		new TGActionProcessor(context, armed ? TGDisarmTrackAction.NAME : TGArmTrackAction.NAME).process();
	}
}
