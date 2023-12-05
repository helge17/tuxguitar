package org.herac.tuxguitar.ui.jfx.event;

import javafx.application.Platform;

import org.herac.tuxguitar.ui.jfx.widget.JFXEventReceiver;

public class JFXSelectionListenerChangeManagerAsync<T> extends JFXSelectionListenerChangeManager<T> {
	
	public JFXSelectionListenerChangeManagerAsync(JFXEventReceiver<?> control) {
		super(control);
	}
	
	public void fireEvent() {
		Platform.runLater(new Runnable() {
			public void run() {
				JFXSelectionListenerChangeManagerAsync.super.fireEvent();
			}
		});
	}
}
