package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.event.UIModifyListener;
import org.herac.tuxguitar.ui.swt.event.SWTModifyListenerManager;
import org.herac.tuxguitar.ui.widget.UITextField;

public class SWTTextField extends SWTText implements UITextField {
	
	private SWTModifyListenerManager modifyListener;
	
	public SWTTextField(SWTContainer<? extends Composite> parent, int style) {
		super(parent, style);
		
		this.modifyListener = new SWTModifyListenerManager(this);
	}
	
	public SWTTextField(SWTContainer<? extends Composite> parent) {
		this(parent, SWT.NONE);
	}
	
	public void addModifyListener(UIModifyListener listener) {
		if( this.modifyListener.isEmpty() ) {
			this.getControl().addModifyListener(this.modifyListener);
		}
		this.modifyListener.addListener(listener);
	}

	public void removeModifyListener(UIModifyListener listener) {
		this.modifyListener.removeListener(listener);
		if( this.modifyListener.isEmpty() ) {
			this.getControl().removeModifyListener(this.modifyListener);
		}
	}
}
