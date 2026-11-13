package app.tuxguitar.audio;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionEvent;
import app.tuxguitar.action.TGActionPostExecutionEvent;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.note.TGChangeNoteAction;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.util.TGContext;

/**
 * Entering a note sounds it: once a note is entered (the note action succeeds), the player plays the beat it is on
 * through the MIDI Port (MidiPlayer.playBeat, on a thread of its own), unless the song is playing. Through the
 * TuxGuitar Synthesizer the beat sounds through its track's sound chain, a VST3 instrument among them. The desktop's
 * note controller (TGUpdateModifiedNoteController) does the same: an app installs one of the two, or each note
 * sounds twice.
 */
public final class TGNoteEntry {

	private TGNoteEntry() {
	}

	public static void install(TGContext context) {
		TGEventManager.getInstance(context).addListener(TGActionPostExecutionEvent.EVENT_TYPE, event -> sound(context, event));
	}

	private static void sound(TGContext context, TGEvent event) {
		TGActionContext action = event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT);
		boolean entered = TGChangeNoteAction.NAME.equals(event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID))
			&& Boolean.TRUE.equals(action.getAttribute(TGChangeNoteAction.ATTRIBUTE_SUCCESS));
		MidiPlayer player = MidiPlayer.getInstance(context);
		if (entered && !player.isRunning()) {
			player.playBeat((TGBeat) action.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		}
	}
}
