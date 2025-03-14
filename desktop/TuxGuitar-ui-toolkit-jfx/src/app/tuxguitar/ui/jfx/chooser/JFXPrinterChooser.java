package app.tuxguitar.ui.jfx.chooser;

import app.tuxguitar.ui.chooser.UIPrinterChooser;
import app.tuxguitar.ui.chooser.UIPrinterChooserHandler;
import app.tuxguitar.ui.jfx.printer.JFXPrinter;
import app.tuxguitar.ui.jfx.widget.JFXWindow;

import javafx.print.PrinterJob;

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
