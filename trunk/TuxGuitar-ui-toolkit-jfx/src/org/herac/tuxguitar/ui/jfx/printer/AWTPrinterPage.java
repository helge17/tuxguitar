package org.herac.tuxguitar.ui.jfx.printer;

import org.herac.tuxguitar.awt.graphics.AWTBufferedPainter;
import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.printer.UIPrinterPage;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class AWTPrinterPage extends JFXComponent<AWTBufferedPainter> implements UIPrinterPage {
	
	private AWTBufferedPainter buffer;
	
	public AWTPrinterPage() {
		super(new AWTBufferedPainter());
	}
	
	public UIPainter getPainter() {
		return this.getControl();
	}
	
	public AWTBufferedPainter getBuffer() {
		return this.buffer;
	}

	public void dispose() {
		this.buffer = this.getControl();
		
		super.dispose();
	}
}
