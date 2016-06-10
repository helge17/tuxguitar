package org.herac.tuxguitar.app.action.impl.view;

import java.util.Iterator;
import java.util.Map;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGToggleViewAction extends TGActionBase {

	public static final String NAME = "action.gui.toggle-view";
	
	public static final String ATTRIBUTE_CONTROLLER = TGToggleViewController.class.getName();
	
	public TGToggleViewAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		TGToggleViewController tgToggleViewController = context.getAttribute(ATTRIBUTE_CONTROLLER);
		tgToggleViewController.toggleView(createViewContext(context));
	}
	
	protected TGViewContext createViewContext(TGActionContext context) {
		TGViewContext tgViewContext = new TGViewContext(this.getContext());
		
		Iterator<Map.Entry<String, Object>> it = context.getAttributes().entrySet().iterator();
		while( it.hasNext() ) {
			Map.Entry<String, Object> entry = it.next();
			tgViewContext.setAttribute(entry.getKey(), entry.getValue());
		}
		
		if( tgViewContext.getAttribute(TGViewContext.ATTRIBUTE_PARENT2) == null ) {
			tgViewContext.setAttribute(TGViewContext.ATTRIBUTE_PARENT2, TGWindow.getInstance(this.getContext()).getWindow());
		}
		
		return tgViewContext;
	}
}
