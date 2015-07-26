package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGActionContextFactory;
import org.herac.tuxguitar.action.TGActionErrorEvent;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.installer.TGActionInstaller;
import org.herac.tuxguitar.app.action.listener.access.TGActionAccessInterceptor;
import org.herac.tuxguitar.app.action.listener.cache.TGUpdateListener;
import org.herac.tuxguitar.app.action.listener.gui.TGActionProcessingListener;
import org.herac.tuxguitar.app.action.listener.lock.TGLockableActionListener;
import org.herac.tuxguitar.app.action.listener.save.TGDocumentModifierListener;
import org.herac.tuxguitar.app.action.listener.save.TGUnsavedDocumentInterceptor;
import org.herac.tuxguitar.app.action.listener.thread.TGSyncThreadInterceptor;
import org.herac.tuxguitar.app.action.listener.undoable.TGUndoableActionListener;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGActionAdapterManager {
	
	private TGContext context;
	private TGActionContextFactory actionContextFactory;
	private TGActionAccessInterceptor actionAccessInterceptor;
	private TGActionIdList keyBindingActionIds;
	
	private TGSyncThreadInterceptor syncThreadInterceptor;
	private TGUnsavedDocumentInterceptor unsavedDocumentInterceptor;
	private TGLockableActionListener lockableActionListener;
	private TGUndoableActionListener undoableActionListener;
	private TGUpdateListener updatableActionListener;
	private TGDocumentModifierListener documentModifierListener;
	
	private TGActionAdapterManager(TGContext context){
		this.context = context;
		this.actionContextFactory = new TGActionContextFactoryImpl();
		this.keyBindingActionIds = new TGActionIdList();
		this.actionAccessInterceptor = new TGActionAccessInterceptor();
		this.syncThreadInterceptor = new TGSyncThreadInterceptor(context);
		this.unsavedDocumentInterceptor = new TGUnsavedDocumentInterceptor(context);
		this.lockableActionListener = new TGLockableActionListener(context);
		this.undoableActionListener = new TGUndoableActionListener(context);
		this.updatableActionListener = new TGUpdateListener(this);
		this.documentModifierListener = new TGDocumentModifierListener();
	}
	
	public void initialize(){
		this.initializeHandlers();
		this.initializeDefaultActions();
	}
	
	private void initializeHandlers(){
		TGActionManager tgActionManager = TGActionManager.getInstance(this.context);
		tgActionManager.setActionContextFactory(this.actionContextFactory);
		tgActionManager.addInterceptor(this.actionAccessInterceptor);
		tgActionManager.addInterceptor(this.syncThreadInterceptor);
		tgActionManager.addInterceptor(this.unsavedDocumentInterceptor);
		
		TGActionProcessingListener processingListener = new TGActionProcessingListener();
		tgActionManager.addPreExecutionListener(processingListener);
		tgActionManager.addPreExecutionListener(this.lockableActionListener);
		tgActionManager.addPreExecutionListener(this.undoableActionListener);
		tgActionManager.addPreExecutionListener(this.updatableActionListener);
		
		tgActionManager.addPostExecutionListener(this.updatableActionListener);
		tgActionManager.addPostExecutionListener(this.undoableActionListener);
		tgActionManager.addPostExecutionListener(this.unsavedDocumentInterceptor);
		tgActionManager.addPostExecutionListener(this.documentModifierListener);
		tgActionManager.addPostExecutionListener(this.lockableActionListener);
		tgActionManager.addPostExecutionListener(processingListener);
		
		tgActionManager.addErrorListener(processingListener);
		tgActionManager.addErrorListener(this.lockableActionListener);
		tgActionManager.addErrorListener(new TGEventListener() {
			public void processEvent(TGEvent event) {
				Throwable t = event.getAttribute(TGActionErrorEvent.PROPERTY_ACTION_ERROR);
				t.printStackTrace();
			}
		});
	}
	
	private void initializeDefaultActions() {		
		TGActionInstaller tgActionInstaller = new TGActionInstaller(this);
		tgActionInstaller.installDefaultActions();
	}
	
	public TGActionIdList getKeyBindingActionIds() {
		return this.keyBindingActionIds;
	}
	
	public TGActionAccessInterceptor getActionAccessInterceptor() {
		return this.actionAccessInterceptor;
	}
	
	public TGSyncThreadInterceptor getSyncThreadInterceptor() {
		return syncThreadInterceptor;
	}

	public TGUnsavedDocumentInterceptor getUnsavedDocumentInterceptor() {
		return unsavedDocumentInterceptor;
	}

	public TGDocumentModifierListener getDocumentModifierListener() {
		return documentModifierListener;
	}

	public TGLockableActionListener getLockableActionListener() {
		return lockableActionListener;
	}

	public TGUndoableActionListener getUndoableActionListener() {
		return undoableActionListener;
	}

	public TGUpdateListener getUpdatableActionListener() {
		return updatableActionListener;
	}
	
	public TGContext getContext() {
		return context;
	}
	
	public static TGActionAdapterManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGActionAdapterManager.class.getName(), new TGSingletonFactory<TGActionAdapterManager>() {
			public TGActionAdapterManager createInstance(TGContext context) {
				return new TGActionAdapterManager(context);
			}
		});
	}
}
