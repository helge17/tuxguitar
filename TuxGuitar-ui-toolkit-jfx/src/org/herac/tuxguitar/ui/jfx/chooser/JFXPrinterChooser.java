package org.herac.tuxguitar.ui.jfx.chooser;

import javafx.print.PrinterJob;

import org.herac.tuxguitar.ui.chooser.UIPrinterChooser;
import org.herac.tuxguitar.ui.chooser.UIPrinterChooserHandler;
import org.herac.tuxguitar.ui.jfx.printer.JFXPrinter;
import org.herac.tuxguitar.ui.jfx.widget.JFXWindow;

public class JFXPrinterChooser implements UIPrinterChooser {

	private JFXWindow window;
	
	public JFXPrinterChooser(JFXWindow window) {
		this.window = window;
	}
	
	public void choose(UIPrinterChooserHandler selectionHandler) {
		JFXPrinter jfxPrinter = null;
		PrinterJob printerJob = PrinterJob.createPrinterJob();
		if( printerJob != null && printerJob.showPrintDialog(this.window.getStage()) ) {
			jfxPrinter = new JFXPrinter(printerJob);
		}
		selectionHandler.onSelectPrinter(jfxPrinter);
	}
}
