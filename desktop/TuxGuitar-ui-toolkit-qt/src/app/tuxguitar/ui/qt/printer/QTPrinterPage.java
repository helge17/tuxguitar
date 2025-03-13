package app.tuxguitar.ui.qt.printer;

import app.tuxguitar.ui.printer.UIPrinterPage;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.qt.resource.QTPainter;
import app.tuxguitar.ui.resource.UIPainter;

public class QTPrinterPage extends QTComponent<QTPainter> implements UIPrinterPage {

	public QTPrinterPage(QTPainter control) {
		super(control);
	}

	public UIPainter getPainter() {
		return this.getControl();
	}
}
