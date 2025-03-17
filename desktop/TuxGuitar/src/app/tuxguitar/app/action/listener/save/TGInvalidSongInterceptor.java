package app.tuxguitar.app.action.listener.save;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import app.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGInvalidSongInterceptor implements TGActionInterceptor {

	public static final String INVALID_SONG_INTERCEPTOR_BY_PASS = "invalidSongInterceptor_byPass";

	private TGContext context;
	private List<String> actionIds;

	public TGInvalidSongInterceptor(TGContext context) {
		this.context = context;
		this.actionIds = new ArrayList<String>();
	}

	public void addActionId(String id) {
		this.actionIds.add(id);
	}
	
	public Runnable createInterceptedActionRunnable(final String actionName, final TGActionContext tgActionContext) {
		Runnable runnable =  new Runnable() {
			public void run() {
				TGActionManager tgActionManager = TGActionManager.getInstance(TGInvalidSongInterceptor.this.context);
				tgActionContext.setAttribute(INVALID_SONG_INTERCEPTOR_BY_PASS, Boolean.TRUE);
				tgActionManager.execute(actionName, tgActionContext);
			}
		};
		return new Runnable() {
			public void run() {
				new Thread(runnable).start();
			}
		};
	}

	@Override
	public boolean intercept(String id, TGActionContext actionContext) throws TGActionException {
		TGSongManager songManager = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGSong song = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		boolean bypass = Boolean.TRUE.equals(actionContext.getAttribute(INVALID_SONG_INTERCEPTOR_BY_PASS));
		
		if (!bypass && this.actionIds.contains(id) && !songManager.isValid(song)) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenViewAction.NAME);
			tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("warning.invalid-song.confirm"));
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_CANCEL);
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_YES);
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, this.createInterceptedActionRunnable(id,actionContext));
			tgActionProcessor.process();
			return true;
		}
		return false;
	}

}
