package org.herac.tuxguitar.ui.swt.printer;

import org.eclipse.swt.printing.Printer;
import org.herac.tuxguitar.ui.printer.UIPrinterJob;
import org.herac.tuxguitar.ui.printer.UIPrinterPage;
import org.herac.tuxguitar.ui.swt.SWTComponent;

public class SWTPrinterJob extends SWTComponent<Printer> implements UIPrinterJob {
	
	private boolean started;
	
	public SWTPrinterJob(Printer printer, String jobName) {
		super(printer);
		
		this.started = this.getControl().startJob(jobName);
	}
	
	public void dispose() {
		if( this.started ) {
			this.getControl().endJob();
			this.started = false;
		}
	}
	
	public boolean isDisposed() {
		return (!this.started);
	}
	
	public UIPrinterPage createPage() {
		return new SWTPrinterPage(this.getControl());
	}
}
