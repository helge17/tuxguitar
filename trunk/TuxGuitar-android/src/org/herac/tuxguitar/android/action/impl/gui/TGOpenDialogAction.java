package org.herac.tuxguitar.android.action.impl.gui;

import java.util.Iterator;
import java.util.Map;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.util.TGContext;

import android.app.Activity;

public class TGOpenDialogAction extends TGActionBase{
	
	public static final String NAME = "action.gui.open-dialog";
	
	public static final String ATTRIBUTE_DIALOG_ACTIVITY = TGActivity.class.getName();
	public static final String ATTRIBUTE_DIALOG_CONTROLLER = TGDialogController.class.getName();
	
	public TGOpenDialogAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		Activity activity = (Activity)context.getAttribute(ATTRIBUTE_DIALOG_ACTIVITY);
		TGDialogController tgDialogController = (TGDialogController)context.getAttribute(ATTRIBUTE_DIALOG_CONTROLLER);
		tgDialogController.showDialog(activity, createDialogContext(context));
	}
	
	protected TGDialogContext createDialogContext(TGActionContext context) {
		TGDialogContext tgDialogContext = new TGDialogContext();
		
		Iterator<Map.Entry<String, Object>> it = context.getAttributes().entrySet().iterator();
		while( it.hasNext() ) {
			Map.Entry<String, Object> entry = it.next();
			tgDialogContext.setAttribute(entry.getKey(), entry.getValue());
		}
		
		return tgDialogContext;
	}
}
