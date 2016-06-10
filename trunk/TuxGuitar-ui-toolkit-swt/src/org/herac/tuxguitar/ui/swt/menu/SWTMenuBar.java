package org.herac.tuxguitar.ui.swt.menu;

import org.eclipse.swt.SWT;
import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;

public class SWTMenuBar extends SWTMenu implements UIMenuBar {
	
	public SWTMenuBar(SWTWindow window) {
		super(window.getControl(), SWT.BAR);
	}
}
