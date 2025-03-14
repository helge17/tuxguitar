package app.tuxguitar.ui.printer;

import app.tuxguitar.ui.UIComponent;

public interface UIPrinterJob extends UIComponent {

	UIPrinterPage createPage();
}
