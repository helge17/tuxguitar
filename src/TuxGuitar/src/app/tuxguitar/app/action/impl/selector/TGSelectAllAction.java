package app.tuxguitar.app.action.impl.selector;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.Selector;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

public class TGSelectAllAction extends TGActionBase {

	public static final String NAME = "action.selection.select-all";

	public TGSelectAllAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		Selector selector = tablature.getSelector();
		TGTrackImpl track = tablature.getCaret().getTrack();
		TGSongManager songMgr = (TGSongManager)context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		if (track != null && track.countMeasures() > 0 && !songMgr.getTrackManager().hasMeasureDurationError(track)) {
			TGMeasure first = track.getMeasure(0);
			TGMeasure last = track.getMeasure(track.countMeasures() - 1);
			if (first.countBeats() > 0 && last.countBeats() > 0) {
				selector.initializeSelection(first.getBeat(0));
				selector.updateSelection(last.getBeat(last.countBeats() - 1));
			}
		}
	}
}
