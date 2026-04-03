package app.tuxguitar.app.view.util;

import app.tuxguitar.editor.util.TGSyncProcess;
import app.tuxguitar.ui.resource.UICursor;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.util.TGContext;

public class TGCursorController {

	private TGContext context;
	private TGSyncProcess process;
	private UIControl control;
	private UICursor cursor;

	public TGCursorController(TGContext context, UIControl control) {
		this.context = context;
		this.control = control;
		this.createSyncProcess();
	}

	public void loadCursor(UICursor cursor) {
		this.cursor = cursor;
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
			this.control.setCursor(this.cursor);
		}
	}

	public UIControl getControl() {
		return control;
	}

	public boolean isControlling(UIControl control) {
		if( control == null || control.isDisposed() ) {
			return false;
		}
		return (!this.control.isDisposed() && this.control.equals(control));
	}
}
