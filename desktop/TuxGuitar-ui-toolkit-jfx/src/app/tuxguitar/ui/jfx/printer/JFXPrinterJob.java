package app.tuxguitar.ui.jfx.printer;

import app.tuxguitar.ui.jfx.JFXComponent;
import app.tuxguitar.ui.printer.UIPrinterJob;
import app.tuxguitar.ui.printer.UIPrinterPage;

public class JFXPrinterJob extends JFXComponent<JFXPrinter> implements UIPrinterJob {

	public JFXPrinterJob(JFXPrinter control) {
		super(control);
	}

	public UIPrinterPage createPage() {
		return new JFXPrinterPage(this);
	}
}
