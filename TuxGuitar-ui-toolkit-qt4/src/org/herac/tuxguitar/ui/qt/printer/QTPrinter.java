package org.herac.tuxguitar.ui.qt.printer;

import org.herac.tuxguitar.ui.printer.UIPrinter;
import org.herac.tuxguitar.ui.printer.UIPrinterJob;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.qt.resource.QTResourceFactory;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

import com.trolltech.qt.core.QRect;
import com.trolltech.qt.gui.QPrinter;

public class QTPrinter extends QTComponent<QPrinter> implements UIPrinter {
	
	private UIResourceFactory resourceFactory;
	
	public QTPrinter(QPrinter control) {
		super(control);
		
		this.resourceFactory = new QTResourceFactory();
	}

	public UIResourceFactory getResourceFactory() {
		return resourceFactory;
	}
	
	public Float getDpiScale() {
		return (this.getControl().logicalDpiX() / 100.0f);
	}
	
	public UIRectangle getBounds() {
		UIRectangle bounds = new UIRectangle();
		QRect pageRect = this.getControl().pageRect();
		bounds.getPosition().setX(pageRect.x());
		bounds.getPosition().setY(pageRect.y());
		bounds.getSize().setWidth(pageRect.width());
		bounds.getSize().setHeight(pageRect.height());
		
		return bounds;
	}
	
	public Integer getStartPage() {
		int fromPage = this.getControl().fromPage();
		return (fromPage > 0 ? fromPage : null);
	}
	
	public Integer getEndPage() {
		int toPage = this.getControl().toPage();
		return (toPage > 0 ? toPage : null);
	}
	
	public UIPrinterJob createJob(String name) {
		return new QTPrinterJob(this);
	}
}
