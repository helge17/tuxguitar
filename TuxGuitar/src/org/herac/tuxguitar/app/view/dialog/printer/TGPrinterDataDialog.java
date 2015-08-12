package org.herac.tuxguitar.app.view.dialog.printer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGPrinterDataDialog {

	public static final String ATTRIBUTE_HANDLER = TGPrinterDataHandler.class.getName();
	
	public void show(final TGViewContext context) {
		TGPrinterDataHandler handler = context.getAttribute(ATTRIBUTE_HANDLER);
		Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		PrintDialog dialog = new PrintDialog(parent, SWT.NULL);
		PrinterData printerData = dialog.open();
		if( printerData != null ) {
			handler.updatePrinterData(printerData);
		}
	}
}
