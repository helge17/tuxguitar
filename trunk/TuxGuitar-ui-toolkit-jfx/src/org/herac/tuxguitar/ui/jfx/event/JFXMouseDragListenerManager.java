package org.herac.tuxguitar.ui.jfx.event;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseDragListenerManager;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.jfx.resource.JFXMouseButton;
import org.herac.tuxguitar.ui.resource.UIPosition;

public class JFXMouseDragListenerManager extends UIMouseDragListenerManager implements UIMouseDownListener, UIMouseUpListener, EventHandler<MouseEvent> {
	
	private JFXComponent<?> control;
	private UIPosition startPosition;
	
	public JFXMouseDragListenerManager(JFXComponent<?> control) {
		this.control = control;
	}
	
	public void handle(MouseEvent event) {
		if( this.startPosition != null ) {
			float dragX = (float)(event.getX()  - this.startPosition.getX());
			float dragY = (float)(event.getY()  - this.startPosition.getY());
			
			this.onMouseDrag(new UIMouseEvent(this.control, new UIPosition(dragX, dragY), JFXMouseButton.getMouseButton(event.getButton())));
		}
	}
	
	public void onMouseDown(UIMouseEvent event) {
		this.startPosition = new UIPosition(event.getPosition().getX(), event.getPosition().getY());
	}
	
	public void onMouseUp(UIMouseEvent event) {
		this.startPosition = null;
	}
}
