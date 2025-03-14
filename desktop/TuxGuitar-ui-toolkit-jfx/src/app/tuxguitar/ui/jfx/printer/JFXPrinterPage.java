package app.tuxguitar.ui.jfx.printer;

import app.tuxguitar.ui.jfx.JFXComponent;
import app.tuxguitar.ui.jfx.resource.JFXPainter;
import app.tuxguitar.ui.printer.UIPrinterPage;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UISize;

import javafx.scene.canvas.Canvas;

public class JFXPrinterPage extends JFXComponent<Canvas> implements UIPrinterPage {

	private JFXPainter painter;
	private JFXPrinterJob printerJob;

	public JFXPrinterPage(JFXPrinterJob printerJob, UISize size) {
		super(new Canvas(size.getWidth(), size.getHeight()));

		this.printerJob = printerJob;
		this.painter = new JFXPainter(this.getControl().getGraphicsContext2D());
	}

	public JFXPrinterPage(JFXPrinterJob printerJob) {
		this(printerJob, printerJob.getControl().getBounds().getSize());
	}

	public UIPainter getPainter() {
		return this.painter;
	}

	public boolean isDisposed() {
		return (this.painter == null || this.painter.isDisposed());
	}

	public void dispose() {
		if(!this.isDisposed() ) {
			this.painter.dispose();
			this.printerJob.getControl().getControl().printPage(this.getControl());
		}
	}
}
