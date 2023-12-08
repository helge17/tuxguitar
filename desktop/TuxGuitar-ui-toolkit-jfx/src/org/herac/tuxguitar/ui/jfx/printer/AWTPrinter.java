package org.herac.tuxguitar.ui.jfx.printer;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import org.herac.tuxguitar.awt.graphics.AWTResourceFactory;
import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.printer.UIPrinter;
import org.herac.tuxguitar.ui.printer.UIPrinterJob;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

public class AWTPrinter extends JFXComponent<PrinterJob> implements UIPrinter {
	
	private UIResourceFactory resourceFactory;
	
	public AWTPrinter(PrinterJob control) {
		super(control);
		
		this.resourceFactory = new AWTResourceFactory();
	}
	
	public UIResourceFactory getResourceFactory() {
		return resourceFactory;
	}
	
	public Float getDpiScale() {
		return 0.5f;
	}
	
	public Float getDpiFontScale() {
		return this.getDpiScale();
	}
	
	public UIRectangle getBounds() {
		PageFormat format = this.getControl().defaultPage();
		UIRectangle bounds = new UIRectangle();
		bounds.getPosition().setX((float) format.getImageableX());
		bounds.getPosition().setY((float) format.getImageableY());
		bounds.getSize().setWidth((float) (format.getImageableX() + format.getImageableWidth()));
		bounds.getSize().setHeight((float) (format.getImageableY() + format.getImageableHeight()));
		
		return bounds;
	}
	
	public Integer getStartPage() {
		return null;
	}
	
	public Integer getEndPage() {
		return null;
	}
	
	public UIPrinterJob createJob(String name) {
		return new AWTPrinterJob(this);
	}
}
