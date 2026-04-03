package app.tuxguitar.app.view.util;

import app.tuxguitar.ui.event.UIPaintEvent;
import app.tuxguitar.ui.event.UIPaintListener;
import app.tuxguitar.util.TGContext;

public class TGBufferedPainterListenerLocked extends TGBufferedPainterLocked implements UIPaintListener {

	public TGBufferedPainterListenerLocked(TGContext context, TGBufferedPainterHandle handle) {
		super(context, handle);
	}

	public void onPaint(UIPaintEvent event) {
		this.paintBufferLocked(event.getPainter());
	}
}
