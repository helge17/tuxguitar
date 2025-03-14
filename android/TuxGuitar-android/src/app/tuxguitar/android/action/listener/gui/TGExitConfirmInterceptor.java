package app.tuxguitar.android.action.listener.gui;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.R;
import app.tuxguitar.android.action.impl.gui.TGExitAction;
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.util.TGContext;

public class TGExitConfirmInterceptor implements TGActionInterceptor {

	private static final String EXIT_ACTION_CONFIRMED = "exitConfirmInterceptor_confirmed";

	private TGActivity activity;
	private TGContext context;

	public TGExitConfirmInterceptor(TGContext context, TGActivity activity) {
		this.context = context;
		this.activity = activity;
	}

	public boolean isExitAction(String id) {
		return (TGExitAction.NAME.equals(id));
	}

	private boolean isActionConfirmed(TGActionContext context) {
		return Boolean.TRUE.equals(context.getAttribute(EXIT_ACTION_CONFIRMED));
	}

	public boolean intercept(final String id, final TGActionContext context) throws TGActionException {
		if( this.isExitAction(id) && !this.isActionConfirmed(context)) {
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
		context.setAttribute(EXIT_ACTION_CONFIRMED, true);

		TGActionManager tgActionManager = TGActionManager.getInstance(this.context);
		tgActionManager.execute(actionId, context);
	}
}
