package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGUpdateLoadedSongController extends TGUpdateItemsController {

	public TGUpdateLoadedSongController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		MidiPlayer midiPlayer = MidiPlayer.getInstance(context);
		midiPlayer.reset();
		midiPlayer.getMode().clear();
		midiPlayer.resetChannels();

		TGUndoableManager.getInstance(context).discardAllEdits();

		// Ensure percussion channel, required for features like metronome and count down
		TGSongManager mgr = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		if (mgr != null) {
			TGSong song = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			if (song != null) {
				mgr.ensurePercussionChannel(song);
			}
		}


		this.findUpdateBuffer(context).requestUpdateLoadedSong();

		super.update(context, actionContext);
	}
}
