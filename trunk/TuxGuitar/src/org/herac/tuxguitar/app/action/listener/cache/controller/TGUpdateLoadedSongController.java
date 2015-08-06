package org.herac.tuxguitar.app.action.listener.cache.controller;

import java.net.URL;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

public class TGUpdateLoadedSongController extends TGUpdateItemsController {

	public static final String ATTRIBUTE_URL = URL.class.getName();
	
	public TGUpdateLoadedSongController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		// ------------------------------------------------------ //
		MidiPlayer midiPlayer = MidiPlayer.getInstance(context);
		midiPlayer.reset();
		midiPlayer.getMode().clear();
		midiPlayer.resetChannels();
		
		URL url = actionContext.getAttribute(ATTRIBUTE_URL);
		TuxGuitar.getInstance().getFileHistory().reset(url);
		if( url != null ) {
			TuxGuitar.getInstance().getFileHistory().setChooserPath( url );
		}
		
		TGUndoableManager.getInstance(context).discardAllEdits();
		
		TuxGuitar.getInstance().getEditorCache().reset();
		TGWindow.getInstance(context).loadTitle();
		// ------------------------------------------------------ //
		
		this.findUpdateBuffer(context, actionContext).requestUpdateLoadedSong();
		
		super.update(context, actionContext);
	}
}
