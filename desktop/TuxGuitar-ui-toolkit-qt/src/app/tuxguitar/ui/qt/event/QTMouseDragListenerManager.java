package app.tuxguitar.ui.qt.event;

import app.tuxguitar.ui.event.UIMouseDownListener;
import app.tuxguitar.ui.event.UIMouseDragListenerManager;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIMouseMoveListener;
import app.tuxguitar.ui.event.UIMouseUpListener;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.resource.UIPosition;

public class QTMouseDragListenerManager extends UIMouseDragListenerManager implements UIMouseDownListener, UIMouseUpListener, UIMouseMoveListener {

	private QTComponent<?> control;
	private UIPosition startPosition;

	public QTMouseDragListenerManager(QTComponent<?> control) {
		this.control = control;
	}

	public void onMouseDown(UIMouseEvent event) {
		this.startPosition = new UIPosition(event.getPosition().getX(), event.getPosition().getY());
	}

	public void onMouseUp(UIMouseEvent event) {
		this.startPosition = null;
	}

	public void onMouseMove(UIMouseEvent event) {
		if( this.startPosition != null ) {
			float dragX = (float) (event.getPosition().getX()  - this.startPosition.getX());
			float dragY = (float) (event.getPosition().getY()  - this.startPosition.getY());

// TODO QT 5->6 //			this.onMouseDrag(new UIMouseEvent(this.control, new UIPosition(dragX, dragY), event.getButton()));
		}
	}
}
