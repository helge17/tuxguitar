package org.herac.tuxguitar.ui.qt.printer;

import org.herac.tuxguitar.ui.printer.UIPrinterPage;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.qt.resource.QTPainter;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class QTPrinterPage extends QTComponent<QTPainter> implements UIPrinterPage {
	
	public QTPrinterPage(QTPainter control) {
		super(control);
	}
	
	public UIPainter getPainter() {
		return this.getControl();
	}
}
