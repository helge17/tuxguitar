package org.herac.tuxguitar.app.view.util;

import org.eclipse.swt.widgets.Control;
import org.herac.tuxguitar.util.TGContext;

public class TGCursorController {
	
	private TGSyncProcess process;
	private int cursorStyle;
	
	public TGCursorController(TGContext context, Control control) {
		this.createSyncProcess(context, control);
	}
	
	public void loadCursor(int cursorStyle) {
		this.cursorStyle = cursorStyle;
		this.process.process();
	}
	
	public void createSyncProcess(final TGContext context, final Control control) {
		this.process = new TGSyncProcess(context, new Runnable() {
			public void run() {
				control.setCursor(control.getDisplay().getSystemCursor(TGCursorController.this.cursorStyle));
			}
		});
	}
}
