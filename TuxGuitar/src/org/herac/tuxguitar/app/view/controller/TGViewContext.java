package org.herac.tuxguitar.app.view.controller;

import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;

public class TGViewContext extends TGAbstractContext {
	
	public static final String ATTRIBUTE_PARENT2 = "parent2";
	public static final String ATTRIBUTE_DISPOSE_LISTENER = UIDisposeListener.class.getName();
	
	private TGContext context;
	
	public TGViewContext(TGContext context){
		this.context = context;
	}
	
	public TGContext getContext() {
		return context;
	}
}
