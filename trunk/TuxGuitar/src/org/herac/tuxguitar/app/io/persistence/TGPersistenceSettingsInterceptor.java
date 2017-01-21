package org.herac.tuxguitar.app.io.persistence;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGWriteFileAction;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.editor.action.file.TGSongPersistenceActionBase;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

public class TGPersistenceSettingsInterceptor implements TGActionInterceptor {
	
	private static final String ATTRIBUTE_DONE = TGPersistenceSettingsInterceptor.class.getName() + "-done";
	private static final String ATTRIBUTE_FILE_FORMAT = TGFileFormat.class.getName();
	private static final String ATTRIBUTE_STREAM_CONTEXT = TGSongStreamContext.class.getName();
	
	private static final String[] INTERCEPTABLE_READ_ACTIONS = new String[] {
		TGReadSongAction.NAME
	};
	
	private static final String[] INTERCEPTABLE_WRITE_ACTIONS = new String[] {
		TGWriteFileAction.NAME,
		TGSaveAsFileAction.NAME,
		TGSaveFileAction.NAME
	};
	
	private TGContext context;
	
	public TGPersistenceSettingsInterceptor(TGContext context) {
		this.context = context;
	}
	
	public TGContext getContext() {
		return context;
	}
	
	public boolean isInterceptable(String id, String[] actionIds) {
		for(String actionId : actionIds) {
			if( actionId.equals(id) ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean intercept(final String id, final TGActionContext context) throws TGActionException {
		if(!this.isDone(context)) {
			if( this.isInterceptable(id, INTERCEPTABLE_READ_ACTIONS)) { 
				return this.processSettingsHandler(id, context, TGPersistenceSettingsMode.READ);
			}
			if( this.isInterceptable(id, INTERCEPTABLE_WRITE_ACTIONS)) { 
				return this.processSettingsHandler(id, context, TGPersistenceSettingsMode.WRITE);
			}
		}
		return false;
	}
	
	private boolean isDone(TGActionContext context) {
		return Boolean.TRUE.equals(context.hasAttribute(ATTRIBUTE_DONE));
	}
	
	public boolean processSettingsHandler(final String id, final TGActionContext actionContext, TGPersistenceSettingsMode mode) {
		actionContext.setAttribute(ATTRIBUTE_DONE, Boolean.TRUE);
		
		TGFileFormat fileFormat = actionContext.getAttribute(ATTRIBUTE_FILE_FORMAT);
		if( fileFormat != null ) {
			TGPersistenceSettingsHandler handler = TGPersistenceSettingsManager.getInstance(this.context).findSettingsHandler(fileFormat, mode);
			if( handler != null ) {
				TGSongStreamContext streamContext = actionContext.getAttribute(ATTRIBUTE_STREAM_CONTEXT);
				if( streamContext == null ) {
					streamContext = new TGSongStreamContext();
					streamContext.addContext(actionContext);
					actionContext.setAttribute(ATTRIBUTE_STREAM_CONTEXT, streamContext);
				}
				
				handler.handleSettings(streamContext, createCallBackThread(id, actionContext));
				
				return true;
			}
		}
		return false;
	}
	
	public void overrideAttributes(TGActionContext actionContext) {
		TGSongStreamContext streamContext = actionContext.getAttribute(ATTRIBUTE_STREAM_CONTEXT);
		if( streamContext != null ) {
			this.overrideAttribute(streamContext, actionContext, TGSongPersistenceActionBase.ATTRIBUTE_FORMAT);
			this.overrideAttribute(streamContext, actionContext, TGSongPersistenceActionBase.ATTRIBUTE_FORMAT_CODE);
			this.overrideAttribute(streamContext, actionContext, TGSongPersistenceActionBase.ATTRIBUTE_MIME_TYPE);
		}
	}
	
	public void overrideAttribute(TGSongStreamContext streamContext, TGActionContext actionContext, String attribute) {
		if( streamContext.hasAttribute(attribute) ) {
			actionContext.setAttribute(attribute, streamContext.getAttribute(attribute));
		}
	}
	
	public Runnable createCallBackThread(final String id, final TGActionContext context) {
		return new Runnable() {
			public void run() {
				new Thread(createCallBackRunnable(id, context)).start();
			}
		};
	}
	
	public Runnable createCallBackRunnable(final String id, final TGActionContext context) {
		return new Runnable() {
			public void run() {
				overrideAttributes(context);
				executeAction(id, context);
			}
		};
	}
	
	public void executeAction(final String id, final TGActionContext context) {
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(id, context);
	}
}
