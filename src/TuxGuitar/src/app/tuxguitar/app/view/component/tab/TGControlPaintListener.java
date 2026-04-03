package app.tuxguitar.app.view.component.tab;

import app.tuxguitar.app.view.util.TGBufferedPainterLocked.TGBufferedPainterHandle;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.widget.UICanvas;

public class TGControlPaintListener implements TGBufferedPainterHandle {

	private TGControl control;

	public TGControlPaintListener(TGControl tablature){
		this.control = tablature;
	}

	public void paintControl(UIPainter painter) {
		this.control.paintTablature(painter);
	}

	public UICanvas getPaintableControl() {
		return this.control.getCanvas();
	}
}
