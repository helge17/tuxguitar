package org.herac.tuxguitar.app.tools.browser.base;

import org.eclipse.swt.widgets.Shell;

public interface TGBrowserFactory {
	
	public String getName();
	
	public String getType();
	
	public TGBrowserSettings dataDialog(Shell parent);
	
	public TGBrowser newTGBrowser(TGBrowserSettings data);
}
