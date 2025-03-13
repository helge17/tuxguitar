package app.tuxguitar.ui.qt.chooser;

import app.tuxguitar.ui.chooser.UIPrinterChooser;
import app.tuxguitar.ui.chooser.UIPrinterChooserHandler;
import app.tuxguitar.ui.qt.printer.QTPrinter;
import app.tuxguitar.ui.qt.widget.QTAbstractWindow;
import org.qtjambi.qt.printsupport.QPrintDialog;
import org.qtjambi.qt.printsupport.QPrinter;
import org.qtjambi.qt.widgets.QDialog;

public class QTPrinterChooser implements UIPrinterChooser {

	private QTAbstractWindow<?> window;

	public QTPrinterChooser(QTAbstractWindow<?> window) {
		this.window = window;
	}

	public void choose(UIPrinterChooserHandler selectionHandler) {
		QTPrinter selection = null;
		QPrintDialog dialog = new QPrintDialog(this.window.getControl());
		if( dialog.exec() == QDialog.DialogCode.Accepted.value() ) {
			QPrinter printer = dialog.printer();
			if( printer != null ) {
				selection = new QTPrinter(printer);
			}
		}
		selectionHandler.onSelectPrinter(selection);
	}
}
