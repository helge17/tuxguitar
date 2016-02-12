package org.herac.tuxguitar.app.view.component.tab;

import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterLocked.TGBufferedPainterHandle;
import org.herac.tuxguitar.graphics.TGPainter;

public class TGControlPaintListener implements TGBufferedPainterHandle {
	
	private TGControl control;
	
	public TGControlPaintListener(TGControl tablature){
		this.control = tablature;
	}
	
	public void paintControl(TGPainter painter) {
		this.control.paintTablature(painter);
	}

	public Composite getPaintableControl() {
		return this.control;
	}
}
