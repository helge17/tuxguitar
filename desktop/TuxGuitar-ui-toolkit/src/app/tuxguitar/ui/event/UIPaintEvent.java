package app.tuxguitar.ui.event;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.resource.UIPainter;

public class UIPaintEvent extends UIEvent {

	private UIPainter painter;

	public UIPaintEvent(UIComponent control, UIPainter painter) {
		super(control);

		this.painter = painter;
	}

	public UIPainter getPainter() {
		return painter;
	}
}
