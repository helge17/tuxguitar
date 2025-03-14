package app.tuxguitar.app.action.listener.save;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.edit.TGOpenMeasureErrorsDialogAction;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.view.dialog.message.TGMessageDialog;
import app.tuxguitar.app.view.dialog.message.TGMessageDialogController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGInvalidSongInterceptor implements TGActionInterceptor {

	private TGContext context;
	private List<String> actionIds;

	public TGInvalidSongInterceptor(TGContext context) {
		this.context = context;
		this.actionIds = new ArrayList<String>();
	}

	public void addActionId(String id) {
		this.actionIds.add(id);
	}

	@Override
	public boolean intercept(String id, TGActionContext actionContext) throws TGActionException {
		TGSongManager songManager = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGSong song = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);

		if (this.actionIds.contains(id) && !songManager.isValid(song)) {
			TGActionManager tgActionManager = TGActionManager.getInstance(context);
			actionContext.setAttribute(TGMessageDialog.ATTRIBUTE_TITLE, TuxGuitar.getProperty("error"));
			actionContext.setAttribute(TGMessageDialog.ATTRIBUTE_STYLE, TGMessageDialog.STYLE_ERROR);
			actionContext.setAttribute(TGMessageDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("error.invalid-song"));
			actionContext.setAttribute(TGMessageDialog.ATTRIBUTE_OK_ACTION_NAME, TGOpenMeasureErrorsDialogAction.NAME);

			actionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGMessageDialogController());
			tgActionManager.execute(TGOpenViewAction.NAME, actionContext);

			return true;
		}
		return false;
	}

}
