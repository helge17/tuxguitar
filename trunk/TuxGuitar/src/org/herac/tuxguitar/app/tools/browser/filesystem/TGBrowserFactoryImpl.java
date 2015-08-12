package org.herac.tuxguitar.app.tools.browser.filesystem;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserData;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserFactoryImpl implements TGBrowserFactory{
	
	private TGContext context;
	
	public TGBrowserFactoryImpl(TGContext context) {
		this.context = context;
	}
	
	public String getType(){
		return "file.system";
	}
	
	public String getName(){
		return TuxGuitar.getProperty("browser.factory.fs.name");
	}
	
	public TGBrowser newTGBrowser(TGBrowserData data) {
		if( data instanceof TGBrowserDataImpl ){
			return new TGBrowserImpl((TGBrowserDataImpl)data);
		}
		return null;
	}
	
	public TGBrowserData parseData(String string) {
		return TGBrowserDataImpl.fromString(string);
	}
	
	public TGBrowserData dataDialog(Shell parent) {
		TGBrowserDataDialog dialog = new TGBrowserDataDialog(context);
		return dialog.open(parent);
	}
}
