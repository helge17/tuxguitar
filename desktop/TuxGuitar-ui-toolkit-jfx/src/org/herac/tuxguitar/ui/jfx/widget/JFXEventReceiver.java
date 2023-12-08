package org.herac.tuxguitar.ui.jfx.widget;

import org.herac.tuxguitar.ui.jfx.JFXComponent;

public abstract class JFXEventReceiver<T> extends JFXComponent<T> {
	
	private boolean ignoreEvents;
	
	public JFXEventReceiver(T component) {
		super(component);
	}

	public boolean isIgnoreEvents() {
		return this.ignoreEvents;
	}

	public void setIgnoreEvents(boolean ignoreEvents) {
		this.ignoreEvents = ignoreEvents;
	}
}
