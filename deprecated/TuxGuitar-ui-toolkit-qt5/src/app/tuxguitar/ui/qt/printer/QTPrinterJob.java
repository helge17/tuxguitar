package app.tuxguitar.ui.qt.printer;

import app.tuxguitar.ui.printer.UIPrinterJob;
import app.tuxguitar.ui.printer.UIPrinterPage;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.qt.resource.QTPainter;
import org.qtjambi.qt.gui.QPainter;

public class QTPrinterJob extends QTComponent<QTPrinter> implements UIPrinterJob {

	private QTPainter painter;

	public QTPrinterJob(QTPrinter control) {
		super(control);

		this.painter = new QTPainter(new QPainter(this.getControl().getControl()));
	}

	public UIPrinterPage createPage() {
		return new QTPrinterPage(this.painter);
	}

	public void dispose() {
		this.painter.dispose();

		super.dispose();
	}
}
