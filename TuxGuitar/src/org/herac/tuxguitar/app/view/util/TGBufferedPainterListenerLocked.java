package org.herac.tuxguitar.app.view.util;

import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UIPaintListener;
import org.herac.tuxguitar.util.TGContext;

public class TGBufferedPainterListenerLocked extends TGBufferedPainterLocked implements UIPaintListener {
	
	public TGBufferedPainterListenerLocked(TGContext context, TGBufferedPainterHandle handle) {
		super(context, handle);
	}

	public void onPaint(UIPaintEvent event) {
		this.paintBufferLocked(event.getPainter());
	}
}
