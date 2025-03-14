package app.tuxguitar.app.action.impl.selector;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateAddedMeasureController;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.Selector;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
