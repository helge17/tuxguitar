package org.herac.tuxguitar.android.action.listener.gui;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.gui.TGFinishAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

public class TGFinishConfirmInterceptor implements TGActionInterceptor {
	
	private static final String FINISH_ACTION_CONFIRMED = "finishConfirmInterceptor_confirmed";
	
	private TGActivity activity;
	private TGContext context;
	
	public TGFinishConfirmInterceptor(TGContext context, TGActivity activity) {
		this.context = context;
		this.activity = activity;
	}
	
	public boolean isFinishAction(String id) {
		return (TGFinishAction.NAME.equals(id));
	}
	
	private boolean isActionConfirmed(TGActionContext context) {
		return Boolean.TRUE.equals(context.getAttribute(FINISH_ACTION_CONFIRMED));
	}
	
	public boolean intercept(final String id, final TGActionContext context) throws TGActionException {
		if( this.isFinishAction(id) && !this.isActionConfirmed(context)) {
			this.processConfirmation(id, context);
			
			return true;
		}
		return false;
	}
	
	public void processConfirmation(final String id, final TGActionContext context) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.activity);
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, this.activity.getString(R.string.global_exit_confirm_message));
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, this.createThreadRunnable(createConfirmedActionRunnable(id, context)));
		tgActionProcessor.process();
	}
	
	public Runnable createThreadRunnable(final Runnable target) {
		return new Runnable() {
			public void run() {
				new Thread(target).start();
			}
		};
	}
	
	public Runnable createConfirmedActionRunnable(final String actionId, final TGActionContext context) {
		return new Runnable() {
			public void run() {
				executeConfirmedAction(actionId, context);
			}
		};
	}
	
	public void executeConfirmedAction(String actionId, TGActionContext context) {
		context.setAttribute(FINISH_ACTION_CONFIRMED, true);
		
		TGActionManager tgActionManager = TGActionManager.getInstance(this.context);
		tgActionManager.execute(actionId, context);
	}
}
