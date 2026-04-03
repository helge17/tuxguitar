package app.tuxguitar.app.view.dialog.printer;

import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.chooser.UIPrinterChooser;
import app.tuxguitar.ui.chooser.UIPrinterChooserHandler;
import app.tuxguitar.ui.printer.UIPrinter;
import app.tuxguitar.ui.widget.UIWindow;

public class TGPrinterChooserDialog {

	public static final String ATTRIBUTE_HANDLER = TGPrinterChooserHandler.class.getName();

	public void show(final TGViewContext context) {
		final TGPrinterChooserHandler handler = context.getAttribute(ATTRIBUTE_HANDLER);
		final UIFactory factory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);

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
