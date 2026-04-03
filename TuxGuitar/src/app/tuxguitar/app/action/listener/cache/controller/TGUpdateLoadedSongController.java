package app.tuxguitar.app.action.listener.cache.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.document.TGDocument;
import app.tuxguitar.app.document.TGDocumentListAttributes;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.app.helper.TGFileHistory;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.transport.TGTransportModeAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;

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
			TGDocument document = tgDocumentListManager.findCurrentDocument();
			document.setUnwanted(unwanted);
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

			TGTransport.getInstance(context).getCache().reset();
			TGWindow.getInstance(context).loadTitle();
			TablatureEditor.getInstance(context).getTablature().restoreStateFrom(document);
			MidiPlayerMode restoredMode = document.getMidiPlayerMode();
			if (restoredMode != null) {
				TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGTransportModeAction.NAME);
				tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_PLAYER_MODE, restoredMode);
				tgActionProcessor.process();
			}

			// ------------------------------------------------------ //

			// Ensure percussion channel, required for features like metronome and count down
			TGDocumentManager tgDocumentManager = TGDocumentManager.getInstance(context);
			tgDocumentManager.getSongManager().ensurePercussionChannel(tgDocumentManager.getSong());

			// ------------------------------------------------------ //

			this.findUpdateBuffer(context, actionContext).requestUpdateLoadedSong();

			super.update(context, actionContext);
		} catch (URISyntaxException e) {
			throw new TGException(e);
		}
	}
}
