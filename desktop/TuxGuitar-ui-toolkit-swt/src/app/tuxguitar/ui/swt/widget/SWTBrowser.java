package app.tuxguitar.ui.swt.widget;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import app.tuxguitar.ui.widget.UIBrowser;

public class SWTBrowser extends SWTControl<Browser> implements UIBrowser {

	public SWTBrowser(SWTContainer<? extends Composite> parent) {
		super(new Browser(parent.getControl(), SWT.NONE), parent);
	}

	@Override
	public void loadUrl(URL url) {
		this.getControl().setUrl(url.toString());
	}

}
