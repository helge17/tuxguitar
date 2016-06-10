package swtimpl.toolbar;

import swtimpl.SWTComponent;

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
