package org.herac.tuxguitar.app.view.component.tab;

import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.app.view.util.TGBufferedPainterLocked.TGBufferedPainterHandle;
import org.herac.tuxguitar.graphics.TGPainter;

public class TablaturePaintListener implements TGBufferedPainterHandle {
	
	private Tablature tablature;
	
	public TablaturePaintListener(Tablature tablature){
		this.tablature = tablature;
	}
	
	public void paintControl(TGPainter painter) {
		this.tablature.paintTablature(painter);
	}

	public Composite getPaintableControl() {
		return this.tablature;
	}
}
