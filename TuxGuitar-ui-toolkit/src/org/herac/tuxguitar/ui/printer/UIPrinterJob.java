package org.herac.tuxguitar.ui.printer;

import org.herac.tuxguitar.ui.UIComponent;

public interface UIPrinterJob extends UIComponent {
	
	UIPrinterPage createPage();
}
