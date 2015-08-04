package org.herac.tuxguitar.app.view.util;

import org.eclipse.swt.widgets.Control;
import org.herac.tuxguitar.util.TGContext;

public class TGCursorController {
	
	private TGContext context;
	private TGSyncProcess process;
	private Control control;
	private int cursorStyle;
	
	public TGCursorController(TGContext context, Control control) {
		this.context = context;
		this.control = control;
		this.createSyncProcess();
	}
	
	public void loadCursor(int cursorStyle) {
		this.cursorStyle = cursorStyle;
		this.process.process();
	}
	
	public void createSyncProcess() {
		this.process = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				TGCursorController.this.updateCursor();
			}
		});
	}
	
	private void updateCursor() {
		if( this.isControlling(this.control)) {
			this.control.setCursor(this.control.getDisplay().getSystemCursor(this.cursorStyle));
		}
	}

	public Control getControl() {
		return control;
	}
	
	public boolean isControlling(Control control) {
		if( control == null || control.isDisposed() ) {
			return false;
		}
		return (!this.control.isDisposed() && this.control.equals(control));
	}
}
