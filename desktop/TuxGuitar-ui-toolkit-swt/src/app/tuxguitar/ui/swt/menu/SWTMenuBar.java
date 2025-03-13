package app.tuxguitar.ui.swt.menu;

import org.eclipse.swt.SWT;
import app.tuxguitar.ui.menu.UIMenuBar;
import app.tuxguitar.ui.swt.widget.SWTWindow;

public class SWTMenuBar extends SWTMenu implements UIMenuBar {

	public SWTMenuBar(SWTWindow window) {
		super(window.getControl(), SWT.BAR);

		window.setMenuBar(this);
	}
}
