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
 * Created by tubus on 20.12.16.
 */
public class TGStartDragSelectionAction extends TGActionBase {

	public static final String NAME = "action.selection.start-drag";

	public TGStartDragSelectionAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		EditorKit editorKit = tablature.getEditorKit();

		if (editorKit.fillSelection(context)) {
			TGActionManager.getInstance(getContext()).execute(TGMoveToAction.NAME, context);
			TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
			Selector selector = tablature.getSelector();
			selector.initializeSelection(beat);
		}
	}
}
