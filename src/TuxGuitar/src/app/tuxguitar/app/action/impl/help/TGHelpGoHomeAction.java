package app.tuxguitar.app.action.impl.help;

import java.net.MalformedURLException;
import java.net.URL;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

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
