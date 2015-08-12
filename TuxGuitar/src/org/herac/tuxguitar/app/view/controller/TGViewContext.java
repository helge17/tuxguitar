package org.herac.tuxguitar.app.view.controller;

import org.eclipse.swt.events.DisposeListener;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;

public class TGViewContext extends TGAbstractContext {
	
	public static final String ATTRIBUTE_PARENT = "parent";
	public static final String ATTRIBUTE_DISPOSE_LISTENER = DisposeListener.class.getName();
	
	private TGContext context;
	
	public TGViewContext(TGContext context){
		this.context = context;
	}
	
	public TGContext getContext() {
		return context;
	}
}
