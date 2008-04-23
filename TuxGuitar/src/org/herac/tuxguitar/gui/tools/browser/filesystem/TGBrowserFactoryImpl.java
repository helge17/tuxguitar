package org.herac.tuxguitar.gui.tools.browser.filesystem;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserData;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserFactory;

public class TGBrowserFactoryImpl implements TGBrowserFactory{
	
	public TGBrowserFactoryImpl() {
		super();
	}
	
	public String getType(){
		return "file.system";
	}
	
	public String getName(){
		return TuxGuitar.getProperty("browser.factory.fs.name");
	}
	
	public TGBrowser newTGBrowser(TGBrowserData data) {
		if(data instanceof TGBrowserDataImpl){
			return new TGBrowserImpl((TGBrowserDataImpl)data);
		}
		return null;
	}
	
	public TGBrowserData parseData(String string) {
		return TGBrowserDataImpl.fromString(string);
	}
	
	public TGBrowserData dataDialog(Shell parent) {
		TGBrowserDataDialog dialog = new TGBrowserDataDialog();
		return dialog.open(parent);
	}
}
