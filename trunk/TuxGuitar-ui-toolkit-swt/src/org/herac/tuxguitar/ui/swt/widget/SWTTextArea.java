package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.event.UIModifyListener;
import org.herac.tuxguitar.ui.swt.event.SWTModifyListenerManager;
import org.herac.tuxguitar.ui.widget.UITextArea;

public class SWTTextArea extends SWTText implements UITextArea {
	
	private SWTModifyListenerManager modifyListener;
	
	public SWTTextArea(SWTContainer<? extends Composite> parent, boolean vScroll, boolean hScroll) {
		super(parent, SWT.MULTI | SWT.WRAP | (vScroll ? SWT.V_SCROLL : 0) | (hScroll ? SWT.H_SCROLL : 0));
		
		this.modifyListener = new SWTModifyListenerManager(this);
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
