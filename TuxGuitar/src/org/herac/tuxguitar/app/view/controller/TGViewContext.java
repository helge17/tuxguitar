package org.herac.tuxguitar.app.view.controller;

import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;

public class TGViewContext extends TGAbstractContext {
	
	public static final String ATTRIBUTE_PARENT = "parent";
	
	private TGContext context;
	
	public TGViewContext(TGContext context){
		this.context = context;
	}
	
	public TGContext getContext() {
		return context;
	}
}
