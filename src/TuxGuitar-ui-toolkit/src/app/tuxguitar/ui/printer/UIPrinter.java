package app.tuxguitar.ui.printer;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UIResourceFactory;

public interface UIPrinter extends UIComponent {

	Float getDpiScale();

	Float getDpiFontScale();

	Integer getStartPage();

	Integer getEndPage();

	UIRectangle getBounds();

	UIResourceFactory getResourceFactory();

	UIPrinterJob createJob(String name);
}
