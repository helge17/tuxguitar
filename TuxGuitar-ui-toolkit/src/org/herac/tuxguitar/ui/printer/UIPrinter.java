package org.herac.tuxguitar.ui.printer;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

public interface UIPrinter extends UIComponent {
	
	Float getDpiScale();
	
	Integer getStartPage();
	
	Integer getEndPage();
	
	UIRectangle getBounds();
	
	UIResourceFactory getResourceFactory();
	
	UIPrinterJob createJob(String name);
}
