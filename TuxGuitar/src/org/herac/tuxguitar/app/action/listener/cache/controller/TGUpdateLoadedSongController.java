package org.herac.tuxguitar.app.action.listener.cache.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.helper.TGFileHistory;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;

public class TGUpdateLoadedSongController extends TGUpdateItemsController {

	public static final String ATTRIBUTE_URL = URL.class.getName();
	
	public TGUpdateLoadedSongController() {
		super();
	}

	@Override
	public void update(TGContext context, TGActionContext actionContext) {
		try {
			// ------------------------------------------------------ //
			MidiPlayer midiPlayer = MidiPlayer.getInstance(context);
			midiPlayer.reset();
			midiPlayer.getMode().clear();
			midiPlayer.resetChannels();
			
			URL url = actionContext.getAttribute(ATTRIBUTE_URL);
			URI uri = actionContext.getAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENT_URI);
			
			Boolean unwanted = Boolean.TRUE.equals(actionContext.getAttribute(TGDocumentListAttributes.ATTRIBUTE_UNWANTED));
			TGDocumentListManager tgDocumentListManager = TGDocumentListManager.getInstance(context);
			tgDocumentListManager.findCurrentDocument().setUnwanted(unwanted);
			tgDocumentListManager.updateLoadedDocument();
			
			if( url != null ) {
				TGFileHistory tgFileHistory = TGFileHistory.getInstance(context);
				tgFileHistory.reset(url);
				tgFileHistory.setChooserPath( url );
				
				if( uri == null ) {
					uri = url.toURI();
				}
			}
			
			if( uri != null ) {
				tgDocumentListManager.findCurrentDocument().setUri(uri);
			}
			
			TuxGuitar.getInstance().getEditorCache().reset();
			TGWindow.getInstance(context).loadTitle();
			// ------------------------------------------------------ //
			
			this.findUpdateBuffer(context, actionContext).requestUpdateLoadedSong();
			
			super.update(context, actionContext);
		} catch (URISyntaxException e) {
			throw new TGException(e);
		}
	}
}
