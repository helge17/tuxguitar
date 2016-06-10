package org.herac.tuxguitar.app.view.dialog.printer;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIPrinterChooser;
import org.herac.tuxguitar.ui.chooser.UIPrinterChooserHandler;
import org.herac.tuxguitar.ui.printer.UIPrinter;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGPrinterChooserDialog {

	public static final String ATTRIBUTE_HANDLER = TGPrinterChooserHandler.class.getName();
	
	public void show(final TGViewContext context) {
		final TGPrinterChooserHandler handler = context.getAttribute(ATTRIBUTE_HANDLER);
		final UIFactory factory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		
		UIPrinterChooser uiPrinterChooser = factory.createPrinterChooser(parent);
		uiPrinterChooser.choose(new UIPrinterChooserHandler() {
			public void onSelectPrinter(UIPrinter printer) {
				if( printer != null ) {
					handler.updatePrinter(printer);
				}
			}
		});
	}
}
