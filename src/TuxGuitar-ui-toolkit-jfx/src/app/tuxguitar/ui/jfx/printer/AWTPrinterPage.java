package app.tuxguitar.ui.jfx.printer;

import app.tuxguitar.awt.graphics.AWTBufferedPainter;
import app.tuxguitar.ui.jfx.JFXComponent;
import app.tuxguitar.ui.printer.UIPrinterPage;
import app.tuxguitar.ui.resource.UIPainter;

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
