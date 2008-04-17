package org.herac.tuxguitar.gui.tools.browser.base;

import org.eclipse.swt.widgets.Shell;

public interface TGBrowserFactory {
	
	public String getName();
	
	public String getType();
	
	public TGBrowserData parseData(String string);
	
	public TGBrowserData dataDialog(Shell parent);
	
	public TGBrowser newTGBrowser(TGBrowserData data);
}
