package org.herac.tuxguitar.ui.qt.printer;

import org.herac.tuxguitar.ui.printer.UIPrinterJob;
import org.herac.tuxguitar.ui.printer.UIPrinterPage;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.qt.resource.QTPainter;

import com.trolltech.qt.gui.QPainter;

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
