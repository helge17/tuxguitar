package org.herac.tuxguitar.android.action;

import org.herac.tuxguitar.android.view.tablature.TGSongView;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGActionBase extends org.herac.tuxguitar.editor.action.TGActionBase {

	public TGActionBase(TGContext context, String name) {
		super(context, name);
	}

	//	
//	public static final String ATTRIBUTE_SUCCESS = "success";
//	
//	private TGContext context;
//	
//	private String name;
//	
//	public TGActionBase(TGContext context, String name) {
//		this.context = context;
//		this.name = name;
//	}
//	
//	protected abstract void processAction(TGActionContext context);
//	
//	public synchronized void execute(final TGActionContext context) {
//		this.processAction(context);
//	}
//	
//	public String getName() {
//		return this.name;
//	}
//	
//	public TGContext getContext() {
//		return context;
//	}
//
//	public TGSongManager getSongManager(TGActionContext actionContext) {
//		return (TGSongManager) actionContext.getAttribute(TGSongManager.class.getName());
//	}
//	
	public TGSongView getEditor() {
		return TGSongView.getInstance(this.getContext());
	}
}
