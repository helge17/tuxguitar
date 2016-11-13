package org.herac.tuxguitar.android.action;

import org.herac.tuxguitar.action.TGActionContextFactory;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.installer.TGActionInstaller;
import org.herac.tuxguitar.android.action.listener.cache.TGUpdateListener;
import org.herac.tuxguitar.android.action.listener.error.TGActionErrorHandler;
import org.herac.tuxguitar.android.action.listener.gui.TGActionProcessingListener;
import org.herac.tuxguitar.android.action.listener.gui.TGExitConfirmInterceptor;
import org.herac.tuxguitar.android.action.listener.gui.TGHideSoftInputListener;
import org.herac.tuxguitar.android.action.listener.lock.TGLockableActionListener;
import org.herac.tuxguitar.android.action.listener.thread.TGSyncThreadInterceptor;
import org.herac.tuxguitar.android.action.listener.transport.TGDisableOnPlayInterceptor;
import org.herac.tuxguitar.android.action.listener.transport.TGStopTransportInterceptor;
import org.herac.tuxguitar.android.action.listener.undoable.TGUndoableActionListener;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGActionAdapterManager {
	
	private TGContext context;
	private TGActionContextFactory actionContextFactory;
	private TGDisableOnPlayInterceptor disableOnPlayInterceptor;
	private TGStopTransportInterceptor stopTransportInterceptor;
	private TGSyncThreadInterceptor syncThreadInterceptor;
	private TGLockableActionListener lockableActionListener;
	private TGUndoableActionListener undoableActionListener;
	private TGUpdateListener updatableActionListener;
	private TGActionErrorHandler errorHandler;
	
	private TGActionAdapterManager(TGContext context){
		this.context = context;
		this.actionContextFactory = new TGActionContextFactoryImpl(context);
		this.disableOnPlayInterceptor = new TGDisableOnPlayInterceptor(context);
		this.stopTransportInterceptor = new TGStopTransportInterceptor(context);
		this.syncThreadInterceptor = new TGSyncThreadInterceptor(context);
		this.lockableActionListener = new TGLockableActionListener(context);
		this.undoableActionListener = new TGUndoableActionListener(context);
		this.updatableActionListener = new TGUpdateListener(this);
		this.errorHandler = new TGActionErrorHandler(context);
	}
	
	public void initialize(TGActivity activity){
		this.initializeHandlers(activity);
		this.initializeDefaultActions();
	}
	
	private void initializeHandlers(TGActivity activity){
		TGActionProcessingListener processingListener = new TGActionProcessingListener(activity);
		TGHideSoftInputListener hideSoftInputListener = new TGHideSoftInputListener(this.getContext(), activity);
		
		TGActionManager tgActionManager = TGActionManager.getInstance(this.getContext());
		tgActionManager.setActionContextFactory(this.actionContextFactory);
		
		tgActionManager.addInterceptor(new TGExitConfirmInterceptor(this.getContext(), activity));
		tgActionManager.addInterceptor(this.stopTransportInterceptor);
		tgActionManager.addInterceptor(this.disableOnPlayInterceptor);
		tgActionManager.addInterceptor(this.syncThreadInterceptor);
		tgActionManager.addInterceptor(this.lockableActionListener);
		
		tgActionManager.addPreExecutionListener(processingListener);
		tgActionManager.addPreExecutionListener(this.errorHandler);
		tgActionManager.addPreExecutionListener(this.lockableActionListener);
		tgActionManager.addPreExecutionListener(this.undoableActionListener);
		tgActionManager.addPreExecutionListener(this.updatableActionListener);
		
		tgActionManager.addPostExecutionListener(this.updatableActionListener);
		tgActionManager.addPostExecutionListener(this.undoableActionListener);
		tgActionManager.addPostExecutionListener(this.lockableActionListener);
		tgActionManager.addPostExecutionListener(this.errorHandler);
		tgActionManager.addPostExecutionListener(hideSoftInputListener);
		tgActionManager.addPostExecutionListener(processingListener);

		tgActionManager.addErrorListener(this.updatableActionListener);
		tgActionManager.addErrorListener(this.undoableActionListener);
		tgActionManager.addErrorListener(this.lockableActionListener);
		tgActionManager.addErrorListener(this.errorHandler);
		tgActionManager.addErrorListener(hideSoftInputListener);
		tgActionManager.addErrorListener(processingListener);

		this.addAsyncProcessStartListener(processingListener);
		this.addAsyncProcessFinishListener(processingListener);
		this.addAsyncProcessErrorListener(processingListener);

		this.addAsyncProcessStartListener(this.errorHandler);
		this.addAsyncProcessFinishListener(this.errorHandler);
		this.addAsyncProcessErrorListener(this.errorHandler);
	}
	
	private void initializeDefaultActions(){
		TGActionInstaller tgActionInstaller = new TGActionInstaller(this);
		tgActionInstaller.installDefaultActions();
	}
	
	public TGContext getContext() {
		return this.context;
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

	public TGLockableActionListener getLockableActionListener() {
		return lockableActionListener;
	}

	public TGUndoableActionListener getUndoableActionListener() {
		return undoableActionListener;
	}

	public TGUpdateListener getUpdatableActionListener() {
		return updatableActionListener;
	}

	public void addAsyncProcessStartListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGActionAsyncProcessStartEvent.EVENT_TYPE, listener);
	}

	public void removeAsyncProcessStartListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGActionAsyncProcessStartEvent.EVENT_TYPE, listener);
	}

	public void addAsyncProcessFinishListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGActionAsyncProcessFinishEvent.EVENT_TYPE, listener);
	}

	public void removeAsyncProcessEndListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGActionAsyncProcessFinishEvent.EVENT_TYPE, listener);
	}

	public void addAsyncProcessErrorListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGActionAsyncProcessErrorEvent.EVENT_TYPE, listener);
	}

	public void removeAsyncProcessErrorListener(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGActionAsyncProcessErrorEvent.EVENT_TYPE, listener);
	}

	public static TGActionAdapterManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGActionAdapterManager.class.getName(), new TGSingletonFactory<TGActionAdapterManager>() {
			public TGActionAdapterManager createInstance(TGContext context) {
				return new TGActionAdapterManager(context);
			}
		});
	}
}
