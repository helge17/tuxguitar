package org.herac.tuxguitar.editor.action;

import org.herac.tuxguitar.action.TGAction;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGActionBase implements TGAction {
	
	public static final String ATTRIBUTE_SUCCESS = "success";
	
	private TGContext context;
	
	private String name;
	
	public TGActionBase(TGContext context, String name) {
		this.context = context;
		this.name = name;
	}
	
	protected abstract void processAction(TGActionContext context);
	
	public synchronized void execute(final TGActionContext context) {
		this.processAction(context);
	}
	
	public String getName() {
		return this.name;
	}
	
	public TGContext getContext() {
		return context;
	}

	public TGSongManager getSongManager(TGActionContext actionContext) {
		return (TGSongManager) actionContext.getAttribute(TGSongManager.class.getName());
	}
}
