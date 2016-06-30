package org.herac.tuxguitar.ui.jfx.printer;

import javafx.print.PageLayout;
import javafx.print.PageRange;
import javafx.print.PrintResolution;
import javafx.print.PrinterJob;

import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.jfx.resource.JFXResourceFactory;
import org.herac.tuxguitar.ui.printer.UIPrinter;
import org.herac.tuxguitar.ui.printer.UIPrinterJob;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

public class JFXPrinter extends JFXComponent<PrinterJob> implements UIPrinter {
	
	private UIResourceFactory resourceFactory;
	
	public JFXPrinter(PrinterJob control) {
		super(control);
		
		this.resourceFactory = new JFXResourceFactory();
	}
	
	public void dispose() {
		this.getControl().endJob();
		
		super.dispose();
	}

	public UIResourceFactory getResourceFactory() {
		return resourceFactory;
	}
	
	public Float getDpiScale() {
		PrintResolution printResolution = this.getControl().getJobSettings().getPrintResolution();
		if( printResolution != null ) {
			return (printResolution.getFeedResolution() / 100.0f);
		}
		return 1f;
	}
	
	public UIRectangle getBounds() {
		UIRectangle bounds = new UIRectangle();
		PageLayout pageLayout = this.getControl().getJobSettings().getPageLayout();
		bounds.getPosition().setX((float) 0);
		bounds.getPosition().setY((float) 0);
		bounds.getSize().setWidth((float) pageLayout.getPrintableWidth());
		bounds.getSize().setHeight((float) pageLayout.getPrintableHeight());
		
		return bounds;
	}
	
	public Integer getStartPage() {
		Integer minimum = null;		
		PageRange[] ranges = this.getControl().getJobSettings().getPageRanges();
		if( ranges != null ) {
			for(PageRange range : ranges) {
				if( minimum == null || range.getStartPage() < minimum ) {
					minimum = range.getStartPage();
				}
			}
		}
		return minimum;
	}
	
	public Integer getEndPage() {
		Integer maximum = null;		
		PageRange[] ranges = this.getControl().getJobSettings().getPageRanges();
		if( ranges != null ) {
			for(PageRange range : ranges) {
				if( maximum == null || range.getEndPage() > maximum ) {
					maximum = range.getEndPage();
				}
			}
		}
		return maximum;
	}
	
	public UIPrinterJob createJob(String name) {
		return new JFXPrinterJob(this);
	}
}
