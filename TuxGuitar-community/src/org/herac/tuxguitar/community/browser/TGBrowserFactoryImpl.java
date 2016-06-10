package org.herac.tuxguitar.community.browser;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserSettings;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserFactoryImpl implements TGBrowserFactory {
	
	private TGContext context;
	private TGBrowserSettings data;
	
	public TGBrowserFactoryImpl(TGContext context){
		this.context = context;
		this.data = new TGBrowserSettingsModel().toBrowserSettings();
	}
	
	public String getName() {
		return "Community Files";
	}
	
	public String getType() {
		return "community";
	}
	
	public TGBrowser newTGBrowser(TGBrowserSettings data) {
		return new TGBrowserImpl(this.context);
	}
	
	public TGBrowserSettings parseData(String string) {
		return this.data;
	}
	
	public TGBrowserSettings dataDialog(UIWindow parent) {
		TGBrowserAuthDialog authDialog = new TGBrowserAuthDialog(this.context);
		authDialog.open( parent );
		if( authDialog.isAccepted() ){
			return this.data;
		}
		return null;
	}

}
