package org.herac.tuxguitar.app.action.listener.cache.controller;

import java.net.URL;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.helper.TGFileHistory;
import org.herac.tuxguitar.app.view.main.TGWindow;
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
		
		Boolean unwanted = Boolean.TRUE.equals(actionContext.getAttribute(TGDocumentListAttributes.ATTRIBUTE_UNWANTED));
		TGDocumentListManager.getInstance(context).findCurrentDocument().setUnwanted(unwanted);
		TGDocumentListManager.getInstance(context).updateLoadedDocument();
		
		TGFileHistory tgFileHistory = TGFileHistory.getInstance(context);
		tgFileHistory.reset(url);
		if( url != null ) {
			tgFileHistory.setChooserPath( url );
		}
		
		TuxGuitar.getInstance().getEditorCache().reset();
		TGWindow.getInstance(context).loadTitle();
		// ------------------------------------------------------ //
		
		this.findUpdateBuffer(context, actionContext).requestUpdateLoadedSong();
		
		super.update(context, actionContext);
	}
}
