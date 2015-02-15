package org.herac.tuxguitar.community.browser;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserData;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserFactoryImpl implements TGBrowserFactory {
	
	private TGContext context;
	private TGBrowserDataImpl data;
	
	public TGBrowserFactoryImpl(TGContext context){
		this.context = context;
		this.data = new TGBrowserDataImpl();
	}
	
	public String getName() {
		return "Community Files";
	}
	
	public String getType() {
		return "community";
	}
	
	public TGBrowser newTGBrowser(TGBrowserData data) {
		return new TGBrowserImpl(this.context, (TGBrowserDataImpl)data);
	}
	
	public TGBrowserData parseData(String string) {
		return this.data;
	}
	
	public TGBrowserData dataDialog(Shell parent) {
		TGBrowserAuthDialog authDialog = new TGBrowserAuthDialog(this.context);
		authDialog.open( parent );
		if( authDialog.isAccepted() ){
			return this.data;
		}
		return null;
	}

}
