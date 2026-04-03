package app.tuxguitar.app.action.impl.selector;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.caret.TGMoveToAction;
import app.tuxguitar.app.view.component.tab.Selector;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.tab.edit.EditorKit;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.util.TGContext;

/**
 * Created by tubus on 21.12.16.
 */
public class TGUpdateDragSelectionAction extends TGActionBase {

	public static final String NAME = "action.selection.update-drag";

	public TGUpdateDragSelectionAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		EditorKit editorKit = tablature.getEditorKit();

		if (editorKit.fillSelection(context, true)) {
			TGActionManager actionManager = TGActionManager.getInstance(getContext());
			TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
			Selector selector = tablature.getSelector();

			if (!selector.isActive() && tablature.getCaret().getSelectedBeat() != null) {
				selector.initializeSelection(tablature.getCaret().getSelectedBeat());
			}
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION, true);
			actionManager.execute(TGMoveToAction.NAME, context);
			if (selector.getStartBeat() != null
					&& beat.getMeasure().getTrack() == selector.getStartBeat().getMeasure().getTrack()
					&& (selector.isActive() || editorKit.isMinDragWidthReached(context))) {
				selector.updateSelection(beat);
			}
		}
	}
}
