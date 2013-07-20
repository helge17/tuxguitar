package org.herac.tuxguitar.app.actions;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionContextFactory;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.app.TuxGuitar;

public class TGActionContextFactoryImpl implements TGActionContextFactory{

	public static final String PROPERTY_SONG_MANAGER = "songManager";
	
	public TGActionContext createActionContext() throws TGActionException {
		TGActionContext tgActionContext = new TGActionContextImpl();
		tgActionContext.setAttribute(PROPERTY_SONG_MANAGER, TuxGuitar.instance().getSongManager());
		
		return tgActionContext;
	}
}
