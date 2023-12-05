package org.herac.tuxguitar.ui.event;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIPainter;

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
