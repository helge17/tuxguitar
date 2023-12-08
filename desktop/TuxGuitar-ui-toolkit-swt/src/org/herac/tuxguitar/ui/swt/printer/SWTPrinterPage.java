package org.herac.tuxguitar.ui.swt.printer;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.Printer;
import org.herac.tuxguitar.ui.printer.UIPrinterPage;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.swt.SWTComponent;
import org.herac.tuxguitar.ui.swt.resource.SWTPainter;

public class SWTPrinterPage extends SWTComponent<Printer> implements UIPrinterPage {
	
	private UIPainter painter;
	
	public SWTPrinterPage(Printer printer) {
		super(printer);
		
		if( this.getControl().startPage() ) {
			this.painter = new SWTPainter(new GC(this.getControl()));
		}
	}
	
	public UIPainter getPainter() {
		return this.painter;
	}
	
	public boolean isDisposed() {
		return (this.painter == null || this.painter.isDisposed());
	}
	
	public void dispose() {
		if(!this.isDisposed() ) {
			this.getPainter().dispose();
			this.getControl().endPage();
		}
	}
}
