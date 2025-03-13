package app.tuxguitar.android.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.player.base.MidiPlayer;
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

		this.findUpdateBuffer(context).requestUpdateLoadedSong();

		super.update(context, actionContext);
	}
}
