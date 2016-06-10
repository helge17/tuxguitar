package org.herac.tuxguitar.app.view.component.tab;

import org.herac.tuxguitar.app.view.util.TGBufferedPainterLocked.TG2BufferedPainterHandle;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.ui.widget.UICanvas;

public class TGControlPaintListener implements TG2BufferedPainterHandle {
	
	private TGControl control;
	
	public TGControlPaintListener(TGControl tablature){
		this.control = tablature;
	}
	
	public void paintControl(TGPainter painter) {
		this.control.paintTablature(painter);
	}

	public UICanvas getPaintableControl() {
		return this.control.getCanvas();
	}
}
