package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.TGControl;
import app.tuxguitar.app.view.component.tabfolder.TGTabFolder;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGScrollPageDownAction extends TGActionBase {

	public static final String NAME = "action.view.scroll-page-down";

	public TGScrollPageDownAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGControl control = TGTabFolder.getInstance(getContext()).findSelectedControl();

		if (control != null) {
			control.scrollVerticalPage(1);
		}
	}
}
