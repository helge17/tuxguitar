package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionContextFactory;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.TGEditorContext;
import org.herac.tuxguitar.song.managers.TGSongManager;

public class TGActionContextFactoryImpl implements TGActionContextFactory{

	public TGActionContext createActionContext() throws TGActionException {
		TGEditorContext tgEditorContext = TuxGuitar.getInstance().getEditorManager().getActiveContext();
		
		TGActionContext tgActionContext = new TGActionContextImpl();
		tgActionContext.setAttribute(TGSongManager.class.getName(), tgEditorContext.getSongManager());
		
		return tgActionContext;
	}
}
