package org.herac.tuxguitar.app.io.stream;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.file.TGExportSongAction;
import org.herac.tuxguitar.app.action.impl.file.TGImportSongAction;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.io.base.TGSongStreamProvider;
import org.herac.tuxguitar.util.TGContext;

public class TGSongStreamActionInterceptor implements TGActionInterceptor {
	
	private static final String ATTRIBUTE_DONE = TGSongStreamActionInterceptor.class.getName() + "-done";
	private static final String ATTRIBUTE_STREAM_CONTEXT = TGSongStreamContext.class.getName();
	private static final String ATTRIBUTE_STREAM_PROVIDER = TGSongStreamProvider.class.getName();
	
	private static final String[] INTERCEPTABLE_ACTIONS = new String[] {
		TGImportSongAction.NAME,
		TGExportSongAction.NAME
	};
	
	private TGContext context;
	
	public TGSongStreamActionInterceptor(TGContext context) {
		this.context = context;
	}
	
	public TGContext getContext() {
		return context;
	}
	
	public boolean isInterceptable(String id) {
		for(String actionId : INTERCEPTABLE_ACTIONS) {
			if( actionId.equals(id) ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean intercept(final String id, final TGActionContext context) throws TGActionException {
		if( this.isInterceptable(id) && !this.isDone(context) ) {
			return this.processSettingsHandler(id, context);
		}
		return false;
	}
	
	private boolean isDone(TGActionContext context) {
		return Boolean.TRUE.equals(context.hasAttribute(ATTRIBUTE_DONE));
	}
	
	public boolean processSettingsHandler(final String id, final TGActionContext actionContext) {
		actionContext.setAttribute(ATTRIBUTE_DONE, Boolean.TRUE);
		
		TGSongStreamProvider provider = actionContext.getAttribute(ATTRIBUTE_STREAM_PROVIDER);
		if( provider != null ) {
			TGSongStreamSettingsHandler handler = TGSongStreamAdapterManager.getInstance(this.context).findSettingsHandler(provider.getProviderId());
			if( handler != null ) {
				TGSongStreamContext streamContext = actionContext.getAttribute(ATTRIBUTE_STREAM_CONTEXT);
				if( streamContext == null ) {
					streamContext = new TGSongStreamContext();
					streamContext.addContext(actionContext);
					actionContext.setAttribute(ATTRIBUTE_STREAM_CONTEXT, streamContext);
				}
				
				handler.handleSettings(streamContext, createExecuteActionThread(id, actionContext));
				
				return true;
			}
		}
		return false;
	}
	
	public Runnable createExecuteActionThread(final String id, final TGActionContext context) {
		return new Runnable() {
			public void run() {
				new Thread(createExecuteActionRunnable(id, context)).start();
			}
		};
	}
	
	public Runnable createExecuteActionRunnable(final String id, final TGActionContext context) {
		return new Runnable() {
			public void run() {
				executeAction(id, context);
			}
		};
	}
	
	public void executeAction(final String id, final TGActionContext context) {
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(id, context);
	}
}
