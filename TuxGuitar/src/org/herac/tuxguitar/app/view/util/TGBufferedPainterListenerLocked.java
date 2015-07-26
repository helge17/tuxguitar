package org.herac.tuxguitar.app.view.util;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.util.TGContext;

public class TGBufferedPainterListenerLocked extends TGBufferedPainterLocked implements PaintListener {
	
	public TGBufferedPainterListenerLocked(TGContext context, TGBufferedPainterHandle handle) {
		super(context, handle);
	}
	
	public void paintControl(PaintEvent e) {
		this.paintBufferLocked(new TGPainterImpl(e.gc));
	}
}
