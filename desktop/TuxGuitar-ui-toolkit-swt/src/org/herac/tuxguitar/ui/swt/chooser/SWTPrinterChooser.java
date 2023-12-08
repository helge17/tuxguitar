package org.herac.tuxguitar.ui.swt.chooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.herac.tuxguitar.ui.chooser.UIPrinterChooser;
import org.herac.tuxguitar.ui.chooser.UIPrinterChooserHandler;
import org.herac.tuxguitar.ui.swt.printer.SWTPrinter;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;

public class SWTPrinterChooser implements UIPrinterChooser {

	private SWTWindow window;
	
	public SWTPrinterChooser(SWTWindow window) {
		this.window = window;
	}
	
	public void choose(UIPrinterChooserHandler selectionHandler) {
		PrintDialog dialog = new PrintDialog(this.window.getControl(), SWT.NULL);
		PrinterData printerData = dialog.open();
		
		selectionHandler.onSelectPrinter(printerData != null ? new SWTPrinter(printerData) : null); 
	}
}
