package org.herac.tuxguitar.app.action.listener.save;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.edit.TGOpenMeasureErrorsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.dialog.message.TGMessageDialog;
import org.herac.tuxguitar.app.view.dialog.message.TGMessageDialogController;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

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
