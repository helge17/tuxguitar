package org.herac.tuxguitar.ui.swt.toolbar;

import org.herac.tuxguitar.ui.swt.SWTComponent;

public abstract class SWTToolControl<T> extends SWTComponent<T> {
	
	private SWTToolBar parent;
	
	public SWTToolControl(T control, SWTToolBar parent) {
		super(control);
		
		this.parent = parent;
	}
	
	public SWTToolBar getParent() {
		return this.parent;
	}
	
	public boolean isDisposed() {
		return this.isControlDisposed();
	}
	
	public abstract void disposeControl();
	
	public abstract boolean isControlDisposed();
}
