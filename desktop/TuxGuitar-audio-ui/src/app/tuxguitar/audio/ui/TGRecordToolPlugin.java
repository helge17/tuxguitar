package app.tuxguitar.audio.ui;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.audio.TGAudioStatus;
import app.tuxguitar.audio.action.TGRecordAction;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.util.TGContext;

/**
 * Tools > Record: records the armed tracks from the caret, on a thread of its own (the action waits until the
 * recording ends: Stop, the end of the song or of the last loop pass, or the audio device stopping).
 */
public class TGRecordToolPlugin extends TGAudioToolPlugin {

	public TGRecordToolPlugin() {
		super("audio.record");
	}

	// greyed unless a recording can start (a device, an armed track, none under way)
	protected boolean isEnabled(TGContext context) {
		return TGAudioStatus.of(context).canRecord();
	}

	protected void doAction(TGContext context) {
		record(context);
	}

	// what Record does, from this item and the Recording window
	static void record(TGContext context) {
		TGActionProcessor processor = new TGActionProcessor(context, TGRecordAction.NAME);
		processor.setAttribute(TGRecordAction.ATTRIBUTE_START, TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getPosition());
		processor.processOnNewThread();
	}
}
