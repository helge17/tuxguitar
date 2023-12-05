package org.herac.tuxguitar.ui.jfx.event;

import javafx.application.Platform;

import org.herac.tuxguitar.ui.jfx.widget.JFXCanvas;

public class JFXPaintListenerManagerAsync extends JFXPaintListenerManager {
	
	public JFXPaintListenerManagerAsync(JFXCanvas control) {
		super(control);
	}
	
	public void fireEvent() {
		Platform.runLater(new Runnable() {
			public void run() {
				if(!JFXPaintListenerManagerAsync.this.getControl().isDisposed()) {
					JFXPaintListenerManagerAsync.super.fireEvent();
				}
			}
		});
	}
}
