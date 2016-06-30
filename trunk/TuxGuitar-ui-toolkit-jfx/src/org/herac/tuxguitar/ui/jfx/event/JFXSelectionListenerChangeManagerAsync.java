package org.herac.tuxguitar.ui.jfx.event;

import javafx.application.Platform;

import org.herac.tuxguitar.ui.jfx.JFXComponent;

public class JFXSelectionListenerChangeManagerAsync<T> extends JFXSelectionListenerChangeManager<T> {
	
	public JFXSelectionListenerChangeManagerAsync(JFXComponent<?> control) {
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
