package app.tuxguitar.app.action.impl.edit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureCopyDialogAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.song.helpers.TGStoredBeatList;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

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
