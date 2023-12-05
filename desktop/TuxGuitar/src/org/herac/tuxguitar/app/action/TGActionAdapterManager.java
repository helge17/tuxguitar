package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGActionContextFactory;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.installer.TGActionInstaller;
import org.herac.tuxguitar.app.action.listener.cache.TGUpdateListener;
import org.herac.tuxguitar.app.action.listener.error.TGActionErrorHandler;
import org.herac.tuxguitar.app.action.listener.gui.TGActionProcessingListener;
import org.herac.tuxguitar.app.action.listener.lock.TGLockableActionListener;
import org.herac.tuxguitar.app.action.listener.save.TGDocumentModifierListener;
import org.herac.tuxguitar.app.action.listener.save.TGUnsavedDocumentInterceptor;
import org.herac.tuxguitar.app.action.listener.thread.TGSyncThreadInterceptor;
import org.herac.tuxguitar.app.action.listener.transport.TGDisableOnPlayInterceptor;
import org.herac.tuxguitar.app.action.listener.transport.TGStopTransportInterceptor;
import org.herac.tuxguitar.app.action.listener.undoable.TGUndoableActionListener;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGActionAdapterManager {
	
	private TGContext context;
	private TGActionContextFactory actionContextFactory;
	private TGDisableOnPlayInterceptor disableOnPlayInterceptor;
	private TGStopTransportInterceptor stopTransportInterceptor;
	private TGActionIdList keyBindingActionIds;
	
	private TGSyncThreadInterceptor syncThreadInterceptor;
	private TGUnsavedDocumentInterceptor unsavedDocumentInterceptor;
	private TGLockableActionListener lockableActionListener;
	private TGUndoableActionListener undoableActionListener;
	private TGUpdateListener updatableActionListener;
	private TGDocumentModifierListener documentModifierListener;
	private TGActionErrorHandler errorHandler;
	
	private TGActionAdapterManager(TGContext context){
		this.context = context;
		this.actionContextFactory = new TGActionContextFactoryImpl();
		this.keyBindingActionIds = new TGActionIdList();
		this.disableOnPlayInterceptor = new TGDisableOnPlayInterceptor(context);
		this.stopTransportInterceptor = new TGStopTransportInterceptor(context);
		this.syncThreadInterceptor = new TGSyncThreadInterceptor(context);
		this.unsavedDocumentInterceptor = new TGUnsavedDocumentInterceptor(context);
		this.lockableActionListener = new TGLockableActionListener(context);
		this.undoableActionListener = new TGUndoableActionListener(context);
		this.updatableActionListener = new TGUpdateListener(this);
		this.documentModifierListener = new TGDocumentModifierListener(context);
		this.errorHandler = new TGActionErrorHandler(context);
	}
	
	public void initialize(){
		this.initializeHandlers();
		this.initializeDefaultActions();
	}
	
	private void initializeHandlers(){
		TGActionManager tgActionManager = TGActionManager.getInstance(this.context);
		tgActionManager.setActionContextFactory(this.actionContextFactory);
		tgActionManager.addInterceptor(this.stopTransportInterceptor);
		tgActionManager.addInterceptor(this.disableOnPlayInterceptor);
		tgActionManager.addInterceptor(this.syncThreadInterceptor);
		tgActionManager.addInterceptor(this.unsavedDocumentInterceptor);
		tgActionManager.addInterceptor(this.lockableActionListener);
		
		TGActionProcessingListener processingListener = new TGActionProcessingListener(this.context);
		tgActionManager.addPreExecutionListener(processingListener);
		tgActionManager.addPreExecutionListener(this.errorHandler);
		tgActionManager.addPreExecutionListener(this.lockableActionListener);
		tgActionManager.addPreExecutionListener(this.undoableActionListener);
		tgActionManager.addPreExecutionListener(this.updatableActionListener);
		
		tgActionManager.addPostExecutionListener(this.updatableActionListener);
		tgActionManager.addPostExecutionListener(this.undoableActionListener);
		tgActionManager.addPostExecutionListener(this.unsavedDocumentInterceptor);
		tgActionManager.addPostExecutionListener(this.documentModifierListener);
		tgActionManager.addPostExecutionListener(this.lockableActionListener);
		tgActionManager.addPostExecutionListener(this.errorHandler);
		tgActionManager.addPostExecutionListener(processingListener);
		
		tgActionManager.addErrorListener(processingListener);
		tgActionManager.addErrorListener(this.lockableActionListener);
		tgActionManager.addErrorListener(this.errorHandler);
	}
	
	private void initializeDefaultActions() {		
		TGActionInstaller tgActionInstaller = new TGActionInstaller(this);
		tgActionInstaller.installDefaultActions();
	}
	
	public TGActionIdList getKeyBindingActionIds() {
		return this.keyBindingActionIds;
	}
	
	public TGDisableOnPlayInterceptor getDisableOnPlayInterceptor() {
		return this.disableOnPlayInterceptor;
	}
	
	public TGStopTransportInterceptor getStopTransportInterceptor() {
		return stopTransportInterceptor;
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
