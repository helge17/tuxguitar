package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionContextFactory;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

public class TGActionContextFactoryImpl implements TGActionContextFactory{

	public TGActionContext createActionContext() throws TGActionException {
		TGDocumentManager tgDocumentManager = TuxGuitar.getInstance().getDocumentManager();
		
		TGActionContext tgActionContext = new TGActionContextImpl();
		tgActionContext.setAttribute(TGDocumentManager.class.getName(), tgDocumentManager);
		tgActionContext.setAttribute(TGSongManager.class.getName(), tgDocumentManager.getSongManager());
		tgActionContext.setAttribute(TGSong.class.getName(), tgDocumentManager.getSong());
		
		return tgActionContext;
	}
}
