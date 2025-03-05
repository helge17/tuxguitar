package org.herac.tuxguitar.app.action.impl.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureCopyDialogAction;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.clipboard.TGClipboard;
import org.herac.tuxguitar.song.helpers.TGStoredBeatList;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;

public class TGCopyAction extends TGActionBase {
	
	public static final String NAME = "action.edit.copy";
	
	public TGCopyAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext){
		if (Boolean.TRUE.equals(tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SELECTION_IS_ACTIVE))) {
			TGBeatRange beats = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
			TGTrack track = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
			TGSongManager songManager = getSongManager(tgActionContext);
			songManager.updatePreciseStart(track);
			TGClipboard.getInstance(this.getContext()).setData(
				new TGStoredBeatList(beats.getBeats(), track.getStrings(), track.isPercussion(), songManager.getFactory()));
		}
		else {
			TGActionManager.getInstance(this.getContext()).execute(TGOpenMeasureCopyDialogAction.NAME, tgActionContext);
		}
	}
}
