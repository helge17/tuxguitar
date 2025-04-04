package app.tuxguitar.ui.swt.widget;

import app.tuxguitar.ui.swt.SWTComponent;

public abstract class SWTEventReceiver<T> extends SWTComponent<T> {

	private boolean ignoreEvents;

	public SWTEventReceiver(T component) {
		super(component);
	}

	public boolean isIgnoreEvents() {
		return this.ignoreEvents;
	}

	public void setIgnoreEvents(boolean ignoreEvents) {
		this.ignoreEvents = ignoreEvents;
	}
}
