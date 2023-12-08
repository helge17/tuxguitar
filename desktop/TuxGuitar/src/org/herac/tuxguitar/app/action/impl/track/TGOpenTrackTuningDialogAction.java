package org.herac.tuxguitar.app.action.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.dialog.track.TGTrackTuningDialogController;
import org.herac.tuxguitar.app.view.dialog.track.TGTrackStringCountDialogController;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenTrackTuningDialogAction extends TGActionBase{
	
	public static final String NAME = "action.gui.open-track-tuning-dialog";
	
	public TGOpenTrackTuningDialogAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext) {
		TGSongManager songManager = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGTrack track = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		
		if( songManager.isPercussionChannel(track.getSong(), track.getChannelId())) {
			tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGTrackStringCountDialogController());
			TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
		} else {
			tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGTrackTuningDialogController());
			TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
		}
	}
}