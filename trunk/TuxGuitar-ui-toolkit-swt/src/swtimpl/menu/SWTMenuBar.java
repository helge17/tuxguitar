package swtimpl.menu;

import org.eclipse.swt.SWT;
import org.herac.tuxguitar.ui.menu.UIMenuBar;

import swtimpl.widget.SWTWindow;

public class SWTMenuBar extends SWTMenu implements UIMenuBar {
	
	public SWTMenuBar(SWTWindow window) {
		super(window.getControl(), SWT.BAR);
	}
}
