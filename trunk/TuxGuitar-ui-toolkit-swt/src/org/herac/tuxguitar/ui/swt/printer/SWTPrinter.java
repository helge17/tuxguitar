package org.herac.tuxguitar.ui.swt.printer;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.herac.tuxguitar.ui.printer.UIPrinter;
import org.herac.tuxguitar.ui.printer.UIPrinterJob;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;
import org.herac.tuxguitar.ui.swt.SWTComponent;
import org.herac.tuxguitar.ui.swt.resource.SWTResourceFactory;

public class SWTPrinter extends SWTComponent<Printer> implements UIPrinter {
	
	private PrinterData data;
	private UIResourceFactory resourceFactory;
	
	public SWTPrinter(PrinterData data) {
		super(new Printer(data));
		
		this.data = data;
		this.resourceFactory = new SWTResourceFactory(this.getControl());
	}
	
	public void dispose() {
		this.getControl().dispose();
	}

	public boolean isDisposed() {
		return this.getControl().isDisposed();
	}

	public UIResourceFactory getResourceFactory() {
		return resourceFactory;
	}
	
	public Float getDpiScale() {
		Point dpi = this.getControl().getDPI();
		if( dpi != null ){
			return ( dpi.x / 100.0f );
		}
		return 1.0f;
	}
	
	public UIRectangle getBounds() {
		Rectangle area = this.getControl().getClientArea();
		return new UIRectangle(area.x, area.y, area.width, area.height);
	}
	
	public Integer getStartPage() {
		return (this.data.scope == PrinterData.PAGE_RANGE ? this.data.startPage : null);
	}
	
	public Integer getEndPage() {
		return (this.data.scope == PrinterData.PAGE_RANGE ? this.data.endPage : null);
	}
	
	public UIPrinterJob createJob(String name) {
		return new SWTPrinterJob(this.getControl(), name);
	}
}
