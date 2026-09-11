package app.tuxguitar.app.action.impl.caret;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.tab.TGControl;
import app.tuxguitar.app.view.component.tabfolder.TGTabFolder;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGGoPageUpAction extends TGActionBase {

	public static final String NAME = "action.caret.go-page-up";

	public TGGoPageUpAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGControl control = TGTabFolder.getInstance(getContext()).findSelectedControl();
		if (control != null && !control.isDisposed()) {
			Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
			Caret caret = tablature.getCaret();
			control.requestCaretVerticalScroll();
			if (caret.movePageUp(control.getVisibleHeight())) {
				if (!Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION))) {
					tablature.getSelector().clearSelection();
				}
			} else {
				control.cancelCaretVerticalScroll();
			}
		}
	}
}
