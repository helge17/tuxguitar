package app.tuxguitar.app.action.impl.settings;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.table.TGTableViewer;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGReloadTableSettingsAction extends TGActionBase {

	public static final String NAME = "action.system.reload-table-settings";

	public TGReloadTableSettingsAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGTableViewer.getInstance(getContext()).loadConfig();
	}
}