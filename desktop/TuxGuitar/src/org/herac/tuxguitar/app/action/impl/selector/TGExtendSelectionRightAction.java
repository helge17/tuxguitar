package org.herac.tuxguitar.app.action.impl.selector;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateAddedMeasureController;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureAction;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGExtendSelectionRightAction extends TGActionBase {

	public static final String NAME = "action.selection.extend-right";

	public TGExtendSelectionRightAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		Selector selector = tablature.getSelector();
		Caret caret = tablature.getCaret();
		if (!selector.isActive()) {
			selector.initializeSelection(caret.getSelectedBeat());
		}
		if(!caret.moveRight()){
			TGSong song =  context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			context.setAttribute(TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER, song.countMeasureHeaders() + 1);
			context.setAttribute(TGUpdateAddedMeasureController.ATTRIBUTE_EXTEND_SELECTION, true);

			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGAddMeasureAction.NAME, context);

			caret.moveRight();
		} else {
			selector.updateSelection(caret.getSelectedBeat());
		}
	}
}
