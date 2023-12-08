package org.herac.tuxguitar.app.action.impl.settings;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.table.TGTableViewer;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGReloadTableSettingsAction extends TGActionBase {
	
	public static final String NAME = "action.system.reload-table-settings";
	
	public TGReloadTableSettingsAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGTableViewer.getInstance(getContext()).loadConfig();
	}
}