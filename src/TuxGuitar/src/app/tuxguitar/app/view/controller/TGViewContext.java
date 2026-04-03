package app.tuxguitar.app.view.controller;

import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.util.TGAbstractContext;
import app.tuxguitar.util.TGContext;

public class TGViewContext extends TGAbstractContext {

	public static final String ATTRIBUTE_PARENT = "parent";
	public static final String ATTRIBUTE_DISPOSE_LISTENER = UIDisposeListener.class.getName();

	private TGContext context;

	public TGViewContext(TGContext context){
		this.context = context;
	}

	public TGContext getContext() {
		return context;
	}
}
