package org.herac.tuxguitar.app.action.impl.selector;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.measure.TGGoNextMeasureAction;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

public class TGExtendSelectionNextAction extends TGActionBase {

	public static final String NAME = "action.selection.extend-next";

	public TGExtendSelectionNextAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		Selector selector = tablature.getSelector();
		Caret caret = tablature.getCaret();
		if (!selector.isActive()) {
			selector.initializeSelection(caret.getSelectedBeat());
		}
		TGMeasure currentMeasure = caret.getMeasure();
		TGMeasureManager measureManager = getSongManager(context).getMeasureManager();
		TGBeat lastBeat = measureManager.getLastBeat(currentMeasure.getBeats());
		TGBeat currentBeat = caret.getSelectedBeat();

		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION, true);
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		if (currentBeat == lastBeat) {
			tgActionManager.execute(TGGoNextMeasureAction.NAME, context);
			if (caret.getSelectedBeat().getStart() >= selector.getInitialBeat().getStart()) {
				moveToMeasureEnd(context, caret);
			}
		} else {
			moveToMeasureEnd(context, caret);
			if (!getSongManager(context).getTrackManager().isLastMeasure(caret.getMeasure()) && caret.getSelectedBeat().getStart() < selector.getInitialBeat().getStart()) {
				caret.moveRight();
			}
		}
		selector.updateSelection(caret.getSelectedBeat());
	}

	private void moveToMeasureEnd(TGActionContext context, Caret caret) {
		TGMeasureImpl measure = caret.getMeasure();
		TGBeat lastBeat = getSongManager(context).getMeasureManager().getLastBeat(measure.getBeats());
		caret.moveTo(caret.getTrack(), measure, lastBeat, caret.getSelectedString().getNumber());
	}
}
