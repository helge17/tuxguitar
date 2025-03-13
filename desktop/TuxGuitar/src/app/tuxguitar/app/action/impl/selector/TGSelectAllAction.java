package org.herac.tuxguitar.app.action.impl.selector;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

public class TGSelectAllAction extends TGActionBase {

	public static final String NAME = "action.selection.select-all";

	public TGSelectAllAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		Selector selector = tablature.getSelector();
		TGTrackImpl track = tablature.getCaret().getTrack();
		if (track != null && track.countMeasures() > 0) {
			TGMeasure first = track.getMeasure(0);
			TGMeasure last = track.getMeasure(track.countMeasures() - 1);
			if (first.countBeats() > 0 && last.countBeats() > 0) {
				selector.initializeSelection(first.getBeat(0));
				selector.updateSelection(last.getBeat(last.countBeats() - 1));
			}
		}
	}
}
