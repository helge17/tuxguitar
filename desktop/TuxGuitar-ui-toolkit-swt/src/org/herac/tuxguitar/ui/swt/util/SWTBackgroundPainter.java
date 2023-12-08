package org.herac.tuxguitar.ui.swt.util;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Control;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.swt.resource.SWTColor;
import org.herac.tuxguitar.ui.swt.widget.SWTControl;

public class SWTBackgroundPainter implements PaintListener {
	
	private boolean listening;
	private SWTControl<? extends Control> control;
	
	public SWTBackgroundPainter(SWTControl<? extends Control> control) {
		this.control = control;
	}
	
	public void update() {
		UIColor color = this.control.getBgColor();
		
		if(!this.listening && color != null) {
			this.control.getControl().addPaintListener(this);
			this.listening = true;
		}
		else if(this.listening && color == null) {
			this.control.getControl().removePaintListener(this);
			this.listening = false;
		}
	}
	
	public void paintControl(PaintEvent e) {
		UIColor color = this.control.getBgColor();
		
		if( color != null && !color.isDisposed()) {
			UISize size = this.control.getBounds().getSize();
			
			e.gc.setBackground(((SWTColor) color).getHandle());
			e.gc.fillRectangle(0, 0, Math.round(size.getWidth()), Math.round(size.getHeight()));
		}
	}
}
