package org.herac.tuxguitar.ui.event;

import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.widget.UIControl;

public class UIPaintEvent extends UIEvent {
	
	private UIPainter painter;
	
	public UIPaintEvent(UIControl control, UIPainter painter) {
		super(control);
		
		this.painter = painter;
	}

	public UIPainter getPainter() {
		return painter;
	}
}
