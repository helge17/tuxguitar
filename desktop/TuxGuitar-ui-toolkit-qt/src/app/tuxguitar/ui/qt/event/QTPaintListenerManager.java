package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIPaintEvent;
import app.tuxguitar.ui.event.UIPaintListenerManager;
import app.tuxguitar.ui.qt.resource.QTPainter;
import app.tuxguitar.ui.qt.widget.QTCanvas;
import io.qt.core.QEvent;
import io.qt.gui.QPainter;

public class QTPaintListenerManager extends UIPaintListenerManager implements QTEventHandler {

	private QTCanvas control;

	public QTPaintListenerManager(QTCanvas control) {
		this.control = control;
	}

	public boolean handle(QEvent event) {
		QPainter qPainter = new QPainter(this.control.getPaintDeviceInterface());
		QTPainter qtPainter = new QTPainter(qPainter);

		this.onPaint(new UIPaintEvent(this.control, qtPainter));

		qtPainter.dispose();

		return true;
	}
}
