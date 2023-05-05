package org.herac.tuxguitar.app.action.impl.selector;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGContext;

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

		if (editorKit.fillSelection(context)) {
			TGActionManager actionManager = TGActionManager.getInstance(getContext());
			TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
			Selector selector = tablature.getSelector();

			if (!selector.isActive() && tablature.getCaret().getSelectedBeat() != null) {
				selector.initializeSelection(tablature.getCaret().getSelectedBeat());
			}
            context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION, true);
			actionManager.execute(TGMoveToAction.NAME, context);
			if (selector.getStartBeat() != null && beat.getMeasure().getTrack() == selector.getStartBeat().getMeasure().getTrack()) {
				selector.updateSelection(beat);
			}
		}
	}
}
