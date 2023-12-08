package org.herac.tuxguitar.ui.jfx.printer;

import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.printer.UIPrinterJob;
import org.herac.tuxguitar.ui.printer.UIPrinterPage;

public class JFXPrinterJob extends JFXComponent<JFXPrinter> implements UIPrinterJob {
	
	public JFXPrinterJob(JFXPrinter control) {
		super(control);
	}
	
	public UIPrinterPage createPage() {
		return new JFXPrinterPage(this);
	}
}
