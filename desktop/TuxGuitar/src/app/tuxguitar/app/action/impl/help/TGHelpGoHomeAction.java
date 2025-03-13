package org.herac.tuxguitar.app.action.impl.help;

import java.net.MalformedURLException;
import java.net.URL;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGHelpGoHomeAction extends TGActionBase {

	public static final String NAME = "action.gui.go-to-homepage";
	private TGContext context;

	public TGHelpGoHomeAction(TGContext context) {
		super(context, NAME);
		this.context = context;
	}

	protected void processAction(TGActionContext context) {
		URL url = null;
		try {
			url = new URL(TGConfigManager.getInstance(this.context).getStringValue(TGConfigKeys.HOMEPAGE_URL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (url != null) {
			TGApplication.getInstance(this.context).getApplication().openUrl(url);
		}
	}
}
