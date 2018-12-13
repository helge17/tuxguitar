package org.herac.tuxguitar.ui.jfx.printer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.awt.graphics.AWTFont;
import org.herac.tuxguitar.awt.graphics.AWTPainter;
import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.printer.UIPrinterJob;
import org.herac.tuxguitar.ui.printer.UIPrinterPage;

public class AWTPrinterJob extends JFXComponent<AWTPrinter> implements UIPrinterJob, Printable {
	
	private List<AWTPrinterPage> pages;
	
	public AWTPrinterJob(AWTPrinter control) {
		super(control);
		
		this.pages = new ArrayList<AWTPrinterPage>();
	}
	
	public UIPrinterPage createPage() {
		AWTPrinterPage page = new AWTPrinterPage();
		
		this.pages.add(page);
		
		return page;
	}
	
	public void dispose() {
		this.getControl().getControl().setPrintable(this);
		
		try {
			this.getControl().getControl().print();
		} catch (PrinterException e) {
			e.printStackTrace();
		}
		
		super.dispose();
	}
	
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if( pageIndex >= this.pages.size() ) {
			return Printable.NO_SUCH_PAGE;
		}
		
		AWTPrinterPage awtPrinterPage = this.pages.get(pageIndex);
		awtPrinterPage.getBuffer().process(new AWTPainter((Graphics2D) graphics));
		
		return Printable.PAGE_EXISTS;
	}
}
