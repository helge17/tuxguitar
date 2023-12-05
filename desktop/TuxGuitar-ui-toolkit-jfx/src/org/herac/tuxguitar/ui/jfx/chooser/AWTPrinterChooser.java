package org.herac.tuxguitar.ui.jfx.chooser;

import java.awt.print.PrinterJob;

import org.herac.tuxguitar.ui.chooser.UIPrinterChooser;
import org.herac.tuxguitar.ui.chooser.UIPrinterChooserHandler;
import org.herac.tuxguitar.ui.jfx.printer.AWTPrinter;

public class AWTPrinterChooser implements UIPrinterChooser {

	public AWTPrinterChooser() {
		super();
	}
	
	public void choose(UIPrinterChooserHandler selectionHandler) {
		AWTPrinter awtPrinter = null;
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		if( printerJob != null && printerJob.printDialog() ) {
			awtPrinter = new AWTPrinter(printerJob);
		}
		selectionHandler.onSelectPrinter(awtPrinter);
	}
}
